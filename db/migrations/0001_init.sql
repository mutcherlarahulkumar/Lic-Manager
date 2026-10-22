-- =============================================================================
-- BLISS Mobile Agent — rebuild data model (PostgreSQL)
-- =============================================================================
-- This schema is a clean reconstruction of the data model recovered from the
-- original app's bundled SQLite databases (agcon.s3db, bmaextra.s3db,
-- licmdb.s3db) plus the feature domains observed in the app.
--
-- Design notes:
--   * All money columns use NUMERIC(14,2) (never float) so commission / premium
--     totals stay exact.
--   * Every table carries created_at / updated_at in UTC.
--   * "portal-sourced" tables (data the original app pulled from LIC portals)
--     keep a `source` + `fetched_at` column so the rebuild can tell real,
--     user-entered data apart from data imported via the integration you will
--     implement yourself. See docs/REBUILD_TODO.md.
--
-- Apply with:  psql "$DATABASE_URL" -f db/migrations/0001_init.sql
-- =============================================================================

BEGIN;

CREATE EXTENSION IF NOT EXISTS "pgcrypto";   -- gen_random_uuid()

-- -----------------------------------------------------------------------------
-- agents — the LIC agent who owns this installation / account.
-- Recovered from agcon.s3db:agmast (agent master).
-- -----------------------------------------------------------------------------
CREATE TABLE agents (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_code      TEXT NOT NULL UNIQUE,            -- LIC agency/agent code
    initial         TEXT,
    full_name       TEXT NOT NULL,
    date_of_birth   DATE,
    address         TEXT,
    marriage_date   DATE,
    branch_code     TEXT,                            -- agmast.brn
    agency_since    DATE,
    club            TEXT,                            -- agent club membership tier
    renewal_date    DATE,
    licence_no      TEXT,
    qualification   TEXT,
    nominee         TEXT,
    is_terminated   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- -----------------------------------------------------------------------------
-- app_users — authentication for the rebuilt app (clean, app-owned auth).
-- The original app mixed several login flows (mpin/pin/otp/merchant). The
-- rebuild uses ONE clean local auth model; external-portal credentials are
-- never stored here — see licportal integration notes.
-- -----------------------------------------------------------------------------
CREATE TABLE app_users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id        UUID REFERENCES agents(id) ON DELETE SET NULL,
    mobile          TEXT NOT NULL UNIQUE,
    email           TEXT UNIQUE,
    password_hash   TEXT,                            -- argon2id/bcrypt; never plaintext
    mpin_hash       TEXT,                            -- optional 4/6-digit app lock
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- -----------------------------------------------------------------------------
-- agencies — agencies the agent manages / tracks (agency_* screens).
-- -----------------------------------------------------------------------------
CREATE TABLE agencies (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id        UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    agency_code     TEXT NOT NULL,
    name            TEXT NOT NULL,
    branch_code     TEXT,
    division        TEXT,
    status          TEXT,                            -- active / terminated / etc.
    club_tier       TEXT,
    ranking         INTEGER,                         -- agency_ranking screen
    extra           JSONB NOT NULL DEFAULT '{}',     -- misc recovered fields
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (agent_id, agency_code)
);

-- -----------------------------------------------------------------------------
-- clients — policyholders / customers (client_* and family_* screens).
-- -----------------------------------------------------------------------------
CREATE TABLE clients (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id        UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    full_name       TEXT NOT NULL,
    date_of_birth   DATE,
    gender          TEXT,
    mobile          TEXT,
    email           TEXT,
    address         TEXT,
    pan             TEXT,
    aadhaar_last4   TEXT,                            -- store only last 4 if at all
    family_head_id  UUID REFERENCES clients(id) ON DELETE SET NULL,  -- family grouping
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX ix_clients_agent ON clients(agent_id);
CREATE INDEX ix_clients_family ON clients(family_head_id);

-- -----------------------------------------------------------------------------
-- policies — LIC policies (policy_*, add_policy, proposal, renewal, status).
-- `source` distinguishes manually-entered vs portal-imported rows.
-- -----------------------------------------------------------------------------
CREATE TABLE policies (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id            UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    client_id           UUID REFERENCES clients(id) ON DELETE SET NULL,
    policy_no           TEXT NOT NULL,
    plan_code           TEXT,                        -- LIC plan/table code
    plan_name           TEXT,
    sum_assured         NUMERIC(14,2),
    premium             NUMERIC(14,2),
    mode                TEXT,                        -- yly/hly/qly/mly/sss
    commencement_date   DATE,
    term_years          INTEGER,
    ppt_years           INTEGER,                     -- premium paying term
    maturity_date       DATE,
    status              TEXT,                        -- inforce/lapsed/paidup/matured/claim
    next_due_date       DATE,
    source              TEXT NOT NULL DEFAULT 'manual',  -- manual | portal_import
    fetched_at          TIMESTAMPTZ,                 -- set when source = portal_import
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (agent_id, policy_no)
);
CREATE INDEX ix_policies_agent ON policies(agent_id);
CREATE INDEX ix_policies_client ON policies(client_id);
CREATE INDEX ix_policies_due ON policies(next_due_date);

-- -----------------------------------------------------------------------------
-- prospects / leads — sales pipeline (prospect_*, lead*, leads_*, recruitment).
-- -----------------------------------------------------------------------------
CREATE TABLE prospects (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id        UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    full_name       TEXT NOT NULL,
    mobile          TEXT,
    email           TEXT,
    source          TEXT,                            -- lead_extractor / referral / manual
    stage           TEXT NOT NULL DEFAULT 'new',     -- new/contacted/quoted/won/lost
    interest        TEXT,                            -- product interest
    follow_up_date  DATE,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX ix_prospects_agent ON prospects(agent_id);
CREATE INDEX ix_prospects_followup ON prospects(follow_up_date);

-- -----------------------------------------------------------------------------
-- commission_summaries — recovered verbatim from licmdb.s3db:CommissionSummary.
-- One row per agent per month/year commission voucher.
-- -----------------------------------------------------------------------------
CREATE TABLE commission_summaries (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id                UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    agent_code              TEXT NOT NULL,
    month                   INTEGER NOT NULL CHECK (month BETWEEN 1 AND 12),
    year                    INTEGER NOT NULL,
    voucher_processed_date  DATE,
    source_file_name        TEXT,
    imported_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (agent_code, month, year)
);

-- premium_commission_details — recovered from licmdb.s3db:PremiumCommissionDetail
CREATE TABLE premium_commission_details (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    summary_id      UUID NOT NULL REFERENCES commission_summaries(id) ON DELETE CASCADE,
    sl_no           INTEGER,
    description     TEXT NOT NULL,
    premium         NUMERIC(14,2) NOT NULL,
    commission      NUMERIC(14,2) NOT NULL,
    is_total        BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_premcomm_summary ON premium_commission_details(summary_id);

-- payment_recovery_details — recovered from licmdb.s3db:PaymentRecoveryDetail
CREATE TABLE payment_recovery_details (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    summary_id      UUID NOT NULL REFERENCES commission_summaries(id) ON DELETE CASCADE,
    sl_no           INTEGER,
    description     TEXT NOT NULL,
    payment         NUMERIC(14,2) NOT NULL,
    recovery        NUMERIC(14,2) NOT NULL,
    is_total        BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_payrec_summary ON payment_recovery_details(summary_id);

-- -----------------------------------------------------------------------------
-- bonus_rates — reference table recovered from bmaextra.s3db:BonusRate (68 rows).
-- Original was a wide column-per-plan table; the rebuild normalises it to
-- (issue-year window, plan code, rate). Seed data loaded by a later migration.
-- -----------------------------------------------------------------------------
CREATE TABLE bonus_rates (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    issue_date_from DATE,
    issue_date_to   DATE,
    plan_code       TEXT NOT NULL,                   -- original wide column name (A, B1, ...)
    rate_per_1000   NUMERIC(10,2) NOT NULL,          -- reversionary bonus per 1000 SA
    UNIQUE (issue_date_from, plan_code)
);

-- -----------------------------------------------------------------------------
-- portal_jobs — bookkeeping for the LIC-portal integration you will implement.
-- The rebuild records WHAT was requested and WHEN, never any portal credentials.
-- The actual fetch is performed by your own licportal implementation.
-- -----------------------------------------------------------------------------
CREATE TABLE portal_jobs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id        UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    kind            TEXT NOT NULL,                   -- premium_status / commission / revival / ...
    params          JSONB NOT NULL DEFAULT '{}',
    status          TEXT NOT NULL DEFAULT 'pending', -- pending/running/done/error
    result_summary  JSONB,
    error           TEXT,
    requested_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    finished_at     TIMESTAMPTZ
);
CREATE INDEX ix_portal_jobs_agent ON portal_jobs(agent_id);

-- -----------------------------------------------------------------------------
-- updated_at trigger
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$
BEGIN NEW.updated_at = now(); RETURN NEW; END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE t TEXT;
BEGIN
  FOR t IN SELECT unnest(ARRAY['agents','app_users','agencies','clients','policies','prospects'])
  LOOP
    EXECUTE format('CREATE TRIGGER trg_%s_updated BEFORE UPDATE ON %I
                    FOR EACH ROW EXECUTE FUNCTION set_updated_at();', t, t);
  END LOOP;
END $$;

COMMIT;
