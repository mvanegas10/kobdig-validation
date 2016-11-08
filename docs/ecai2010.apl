// Initial mental state of the agent in the running example of the ECAI 2010 paper: 
agent(ECAI2010-Dr.Gent)
{
    knowledge { }

    // Dr. Gent's initial belief base:
    beliefs
    {
	  // If the hotels are full, he will not succeed in booking a hotel room:
	  not f or not h,
	  
	  // If all planes are booked out, he will not succeed in reserving a flight:
	  not b or not t : 0.9,
	  
	  // A paper is presented only if it is accepted and registration is paid:
	  (r and a) or not p
    }

    desires
	{
	  // If he believes the paper is accepted and wants to present it,
	  // then he wants to reserve a plane ticket and book a hotel room:
	  if B(a) and D(p) then (t and h),
	  
	  // If he believes the paper is accepted and Dr. Flaky is unavailable,
	  // he is willing to present it:
	  if B(a and q) then p,
	  
	  // If he believes the paper is accepted, he is willing to pay the registration:
	  if B(a) then r
	}

    obligations { }
}
