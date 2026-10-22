// Package domain holds the core data types shared across the API, store, and
// integration layers. These mirror db/migrations/0001_init.sql.
package domain

import "time"

// Agent is the LIC agent who owns the account. (agents table)
type Agent struct {
	ID           string     `json:"id"`
	AgentCode    string     `json:"agentCode"`
	FullName     string     `json:"fullName"`
	BranchCode   string     `json:"branchCode,omitempty"`
	Club         string     `json:"club,omitempty"`
	AgencySince  *time.Time `json:"agencySince,omitempty"`
	IsTerminated bool       `json:"isTerminated"`
}

// Agency is an agency the agent manages/tracks. (agencies table)
type Agency struct {
	ID         string `json:"id"`
	AgencyCode string `json:"agencyCode"`
	Name       string `json:"name"`
	BranchCode string `json:"branchCode,omitempty"`
	Status     string `json:"status,omitempty"`
	ClubTier   string `json:"clubTier,omitempty"`
	Ranking    int    `json:"ranking,omitempty"`
}

// Client is a policyholder/customer. (clients table)
type Client struct {
	ID          string     `json:"id"`
	FullName    string     `json:"fullName"`
	DateOfBirth *time.Time `json:"dateOfBirth,omitempty"`
	Mobile      string     `json:"mobile,omitempty"`
	Email       string     `json:"email,omitempty"`
	Address     string     `json:"address,omitempty"`
	FamilyHead  string     `json:"familyHeadId,omitempty"`
}

// Policy is a LIC policy. (policies table)
type Policy struct {
	ID               string     `json:"id"`
	ClientID         string     `json:"clientId,omitempty"`
	PolicyNo         string     `json:"policyNo"`
	PlanCode         string     `json:"planCode,omitempty"`
	PlanName         string     `json:"planName,omitempty"`
	SumAssured       float64    `json:"sumAssured,omitempty"`
	Premium          float64    `json:"premium,omitempty"`
	Mode             string     `json:"mode,omitempty"`
	CommencementDate *time.Time `json:"commencementDate,omitempty"`
	Status           string     `json:"status,omitempty"`
	NextDueDate      *time.Time `json:"nextDueDate,omitempty"`
	Source           string     `json:"source"` // manual | portal_import
}

// Prospect is a sales-pipeline lead. (prospects table)
type Prospect struct {
	ID           string     `json:"id"`
	FullName     string     `json:"fullName"`
	Mobile       string     `json:"mobile,omitempty"`
	Stage        string     `json:"stage"`
	Interest     string     `json:"interest,omitempty"`
	FollowUpDate *time.Time `json:"followUpDate,omitempty"`
}

// CommissionSummary + lines. (commission_summaries / *_details tables)
type CommissionSummary struct {
	ID                   string                  `json:"id"`
	AgentCode            string                  `json:"agentCode"`
	Month                int                     `json:"month"`
	Year                 int                     `json:"year"`
	VoucherProcessedDate *time.Time              `json:"voucherProcessedDate,omitempty"`
	PremiumCommission    []PremiumCommissionLine `json:"premiumCommission"`
	PaymentRecovery      []PaymentRecoveryLine   `json:"paymentRecovery"`
}

type PremiumCommissionLine struct {
	SlNo        int     `json:"slNo"`
	Description string  `json:"description"`
	Premium     float64 `json:"premium"`
	Commission  float64 `json:"commission"`
	IsTotal     bool    `json:"isTotal"`
}

type PaymentRecoveryLine struct {
	SlNo        int     `json:"slNo"`
	Description string  `json:"description"`
	Payment     float64 `json:"payment"`
	Recovery    float64 `json:"recovery"`
	IsTotal     bool    `json:"isTotal"`
}
