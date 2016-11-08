// Initial mental state of an investor agent:
agent(InvestorAgent)
{
    knowledge { }
    // Initial belief base of Investor Agent:
    beliefs
    {

    }
    desires
	{
        // If the investor beliefs that, in the current state, selling is more rentable than
        // continuing with the current property as a landlord and desires not to speculate,
        // then the investor desires selling
        if B(sr) and D(not sp) then D(sl)

        // If the investor beliefs that, in the current state, continuing with the current property
        // is more rentable than selling, then the investor decides not to sell
        if B(not sr) then D(not sl)

        // If the investor desires to speculate, then the investor decides not to sell
        if D(sp) then D(not sl)

        // If the investor beliefs it affords buying and has buying rentability and to invest,
        // then the investor desires to buy and create a new property-investor relation
        if B(ab and br) and D(i) then D(b and l)

	}

    obligations { }
}
