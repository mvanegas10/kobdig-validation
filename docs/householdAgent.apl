// Initial mental state of a household agent:
agent(HouseholdAgent)
{
    knowledge { }

    // Initial belief base of Household Agent:
    beliefs
    {

    }

    desires
		{
			// The investing degree for this household is α:
			if 0.5 then i,

			// If the household doesn't desires to invest, the household doesn't desires to
			// become a landlord
			if D(not i) then not l,

			// If the household beliefs it affords buying and the household has no place to 
			// live, then the household desires to buy 
			if B(ab and (not r) and (not o)) then (b and o),

			// If the household beliefs it affords buying and the household desires to change
			// then the household desires to buy and 
			if B(ab) and D(ch and s) then (b and o),

			// If the household beliefs it can't affords buying but believes it affords renting
			// then the household desires to rent:
			if B((not ab) and ar) then r,

			// If the household beliefs it is owner and it is not satisfied, then the
			// household desires to change and either sell or become an investor:
			if B(o and p) then (ch and (s or l)),

			// If the household beliefs it affords buying and has buying rentability and desires
			// to invest, then the household desires to buy and become an investor:
			if B(ab and br) and D(i) then (b and l),

		}

    obligations { }
}
