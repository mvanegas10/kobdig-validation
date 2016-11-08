// The "gourmet" agent used in the examples of the ECAI 2008 paper 
agent(Gourmet)
{
    knowledge { }

    // Initial state when the gourmet steps into a new restaurant:
    beliefs
    {
      ff : 0.2,
      not ge
    }

    desires
    {
        // if you believe they have fresh fish, then you want to have fish:
        if B(ff) then hf,
        
        // if you believe they have great escargots and you don't want to have fish,
        // then you want to have escargots:
        if B(ge) and D(not hf) then he,
        
        // if you want to have meat, you will also want to drink red wine:
        if D(hm) then rw,
        
        // if you want to have fish, you will also want to drink white wine:
        if D(hf) then ww,
        
        // if you want to have fish, you don't want to have meat:
        if D(hf) then not hm,
        
        // you usually want to have meat to the degree 0.7:
        if 0.7 then hm,
        
        // if you drink red wine, you shouldn't drink white wine, and vice versa:
        if D(ww) then not rw,
        if D(rw) then not ww
    }

    obligations { }
}
