-- Legacy schema for the Balam LIC CRM SQLite backup export.

-- Types inferred from the seed's values; adjust if your real data differs.

-- Data-only seed loads on top of this. Contains NO data (safe to commit).


CREATE TABLE IF NOT EXISTS login_attempts (
    _id BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS rpt_prcalendar (
    _id BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS rpt_currstatus (
    _id BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS rpt_cashinout (
    _id BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS rpt_cashflow (
    _id BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS prospect_activity (
    _id BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS leaddata (
    _id BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS documents (
    _id BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS bankdetails (
    _id BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS fupupdate (
    _id BIGINT PRIMARY KEY,
    policy_no TEXT,
    oldfup TEXT,
    newfup TEXT,
    name TEXT,
    dateupdated TEXT
);

CREATE TABLE IF NOT EXISTS fuphistory (
    _id BIGINT PRIMARY KEY,
    policy_no TEXT,
    oldfup TEXT,
    newfup TEXT,
    name TEXT,
    dateupdated TEXT
);

CREATE TABLE IF NOT EXISTS multipledue (
    _id BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS sb (
    _id BIGINT PRIMARY KEY,
    policy_no TEXT,
    sb_duedate TEXT,
    sb_amount TEXT,
    i_no TEXT,
    sb_paydate TEXT,
    ch_no TEXT,
    details TEXT
);

CREATE TABLE IF NOT EXISTS commission (
    _id BIGINT PRIMARY KEY,
    policy_no TEXT,
    bill_date TEXT,
    first_comm TEXT,
    second_comm TEXT,
    third_comm TEXT,
    bonus_comm TEXT,
    single_comm TEXT,
    sub_comm TEXT,
    paydate TEXT
);

CREATE TABLE IF NOT EXISTS loan (
    _id BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS policies (
    _id BIGINT PRIMARY KEY,
    policy_no TEXT,
    familycode TEXT,
    perscode TEXT,
    issue_date TEXT,
    mat_date TEXT,
    payment_mode TEXT,
    premium TEXT,
    sum_assured TEXT,
    plan TEXT,
    plan_name TEXT,
    term TEXT,
    ppt TEXT,
    next_premium TEXT,
    mat_amount TEXT,
    nominee TEXT,
    relation TEXT,
    agcode TEXT,
    stat_cd TEXT,
    status TEXT,
    fupstatus TEXT,
    age TEXT,
    lastpaid TEXT,
    neft TEXT,
    lastfup TEXT,
    branch TEXT,
    dab TEXT,
    termrider TEXT
);

CREATE TABLE IF NOT EXISTS client (
    _id BIGINT PRIMARY KEY,
    familycode TEXT,
    perscode TEXT,
    name TEXT,
    mobileno TEXT,
    dob_r TEXT,
    sex TEXT,
    address TEXT,
    email TEXT,
    age TEXT,
    client_type TEXT,
    comment TEXT,
    source TEXT,
    city TEXT,
    state TEXT,
    occupation TEXT
);

CREATE TABLE IF NOT EXISTS family (
    _id BIGINT PRIMARY KEY,
    familycode TEXT,
    headname TEXT,
    address TEXT,
    email TEXT,
    mobileno TEXT,
    pincode TEXT,
    religion TEXT,
    designation TEXT,
    last_update TEXT
);

CREATE TABLE IF NOT EXISTS plans (
    plan_no TEXT,
    plan_name TEXT,
    plan_type TEXT,
    term_ppt TEXT,
    sb_year TEXT,
    sb_benefit TEXT,
    stax TEXT,
    lapsdays TEXT
);

CREATE TABLE IF NOT EXISTS agent (
    _id BIGINT PRIMARY KEY,
    name TEXT,
    address TEXT,
    mobile TEXT,
    email TEXT,
    company TEXT,
    aboutus TEXT,
    photo TEXT,
    slogan TEXT,
    login TEXT,
    password TEXT,
    website TEXT,
    isprimary BOOLEAN,
    autopass BOOLEAN,
    dob TEXT,
    userkey TEXT,
    mpin TEXT,
    newpassword TEXT,
    ag_since TEXT,
    branch TEXT,
    club TEXT,
    clia_id TEXT,
    machine_id TEXT,
    newuserkey TEXT,
    login_mode TEXT,
    marriagedate TEXT,
    terminated BOOLEAN,
    ren_dt TEXT,
    licence_no TEXT,
    intimation BIGINT,
    scheduling BIGINT,
    report BIGINT,
    authtoken TEXT,
    authjson TEXT,
    devicedata TEXT,
    newpass TEXT,
    devicename TEXT,
    devicetoken TEXT,
    newportal BOOLEAN,
    pan TEXT
);
