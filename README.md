# STM32CubeMX Unofficial Bugfix/Patching

As nice as STM32CubeMX can be for generating and updating STM32X projects
it has a number of bugs. I got annoyed enough to fix one.

This repository was created primarily so I could send ST a bug report.
As well, to help anybody who encounters an issue in the mean time.

## CAN

The CAN manager has a typo for a constant. This is reflected in the CanConstants.java file.

  public static final String Parameter_BitSegment1Cte = "BS1";
  public static final String Parameter_BitSegment2Cte = "BS2";

However it should really be:

  public static final String Parameter_BitSegment1Cte = "TimeSeg1";
  public static final String Parameter_BitSegment2Cte = "TimeSeg2";

I decompiled the class, rebuilt it using the proper constants. Since it was a decompiled
class the references to CanConstants were lost so it ended up being a find/replace job.

Copy can/can.jar to STM32CubeFX/plugins/ip to fix the CAN prescaler exception.
