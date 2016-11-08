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
			// If the household beliefs it affords buying and the household desires to change,
			// then the household desires to buy
			if B(ab) and D(ch) then D(b)

			// If the household beliefs it can't affords buying but believes it affords renting
			// then the household desires to rent
			if B((not ab) and ar) then D(r)

			// If the household beliefs it is owner occupied and it is not satisfied, then the
			// household desires to change and either sell or become an investor
			if B(o and p) then D(ch and (s or l))

			// If the household beliefs it affords buying and has buying rentability and desires
			// to invest, then the household desires to buy and become an investor
			if B(ab and br) and D(i) then D(b and l)

		}

    obligations { }
}
