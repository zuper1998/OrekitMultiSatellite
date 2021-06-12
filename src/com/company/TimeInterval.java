package com.company;

import org.orekit.time.AbsoluteDate;

public class TimeInterval {
    public AbsoluteDate start;
    public AbsoluteDate end;
    public TimeInterval(AbsoluteDate s,AbsoluteDate e){
        start=s;
        end=e;

    }
}
