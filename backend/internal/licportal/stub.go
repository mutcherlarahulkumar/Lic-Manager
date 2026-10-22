package licportal

import (
	"context"
	"fmt"
	"time"
)

// StubClient is a no-network implementation of Client that returns deterministic
// dummy data. It lets the backend and app run fully during the rebuild. Replace
// it with your authorized implementation; keep the same interface.
type StubClient struct{}

// NewStubClient returns a Client that produces dummy portal data.
func NewStubClient() Client { return &StubClient{} }

func (s *StubClient) FetchPremiumStatus(_ context.Context, _ Credentials, policyNo string) (PremiumStatus, error) {
	// DUMMY DATA — replace with real fetch. See licportal.go TODOs.
	return PremiumStatus{
		PolicyNo:    policyNo,
		Status:      "inforce",
		NextDueDate: time.Now().AddDate(0, 2, 0).Format("2006-01-02"),
		DueAmount:   12450.00,
		FetchedAt:   time.Now().UTC(),
	}, nil
}

func (s *StubClient) FetchCommissionStatement(_ context.Context, cr Credentials, month, year int) (CommissionStatement, error) {
	// DUMMY DATA — replace with real fetch. See licportal.go TODOs.
	return CommissionStatement{
		AgentCode: orDefault(cr.AgentCode, "DEMO-AG-001"),
		Month:     month,
		Year:      year,
		TotalComm: 48620.75,
		FetchedAt: time.Now().UTC(),
	}, nil
}

func (s *StubClient) FetchRevivalQuote(_ context.Context, _ Credentials, policyNo string) (RevivalQuote, error) {
	// DUMMY DATA — replace with real fetch. See licportal.go TODOs.
	return RevivalQuote{
		PolicyNo:   policyNo,
		RevivalAmt: 33210.00,
		LateFee:    845.00,
		ValidUpto:  time.Now().AddDate(0, 1, 0).Format("2006-01-02"),
		FetchedAt:  time.Now().UTC(),
	}, nil
}

func orDefault(v, def string) string {
	if v == "" {
		return def
	}
	return v
}

// compile-time check that the stub satisfies the interface.
var _ Client = (*StubClient)(nil)

// ensure fmt is referenced if you add debug logging later.
var _ = fmt.Sprintf
