// Initial mental state of a promotor agent:
agent(PromotorAgent)
{
    knowledge { }

    // Initial belief base of Promotor Agent:
    beliefs
    {

    }

    desires
	{
        // The risk aversion for this promoter is γ:
        if 0.3 then ra,

        // If the promoter beliefs he affords to buy the land and doesn't affords to construct
        // and desires not to be risk averse, then he desires to buy the land and sale off
        // plans:
        if B(abl and not ac) and D(not ra) then (bl and sop),
	}

    obligations { }
}
