// Initial mental state of a promoter agent:
agent(PromoterAgent)
{
    knowledge { }

    // Initial belief base of Promoter Agent:
    beliefs
    {

    }

    desires
	{
        // The risk aversion for this promoter is γ:
        // if 0.5 then not ra,

        // If the promoter beliefs he affords to buy the land and doesn't affords to construct
        // and desires not to be risk averse, then he desires to buy the land and sale off
        // plans:
        if B(abl and not ac) and D(not ra) then (bl and sop),
	}

    obligations { }
}
