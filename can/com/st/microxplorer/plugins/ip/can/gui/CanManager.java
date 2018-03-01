/*     */ package com.st.microxplorer.plugins.ip.can.gui;
/*     */ 
/*     */ import com.st.microxplorer.mcu.IP;
/*     */ import com.st.microxplorer.mcu.Mcu;
/*     */ import com.st.microxplorer.mcu.RCCService;
/*     */ import com.st.microxplorer.mcu.RefParameter;
/*     */ import com.st.microxplorer.mxsystem.MxSystem;
/*     */ import com.st.microxplorer.plugins.ipmanager.generictreatment.model.ParamManager;
/*     */ import com.st.microxplorer.plugins.ipmanager.generictreatment.model.Parameter;
/*     */ import java.awt.Component;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.JOptionPane;
/*     */ 
/*     */ public class CanManager
/*     */   implements CanConstants
/*     */ {
/*     */   private IP m_clIp;
/*     */   private RCCService m_clRccService;
/*     */   private ParamManager m_clParameterManager;
/*     */   private String m_clIpInstanceName;
/*     */   private List<String> m_clParamInvalidNameList;
/*     */ 
/*     */   public CanManager(IP clIp, ParamManager clParameterManager)
/*     */   {
/*  41 */     this.m_clIp = clIp;
/*  42 */     this.m_clParameterManager = clParameterManager;
/*     */ 
/*  45 */     this.m_clParameterManager.updateIpParameters();
/*  46 */     if (this.m_clParameterManager.getIpParameters().size() != 0)
/*     */     {
/*  48 */       if (this.m_clRccService == null) {
/*  49 */         this.m_clRccService = MxSystem.getMxMcu().getRCCService();
/*     */       }
/*  51 */       if (this.m_clIpInstanceName == null) {
/*  52 */         this.m_clIpInstanceName = this.m_clIp.getInstanceName();
/*     */       }
/*     */ 
/*  56 */       clearParamInvalidList();
/*     */ 
/*  59 */       clockChange();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clockChange()
/*     */   {
/*  68 */     List clUpdateParamList = null;
/*     */ 
/*  70 */     Parameter clParameter = getParameter("Prescaler");
/*  71 */     if (clParameter != null) {
/*  72 */       clUpdateParamList = verifyValuePrescaler(null, clParameter, true);
/*     */ 
/*  75 */       if (clUpdateParamList != null) {
/*  76 */         for (Parameter clParamLoop : clUpdateParamList) {
/*  77 */           this.m_clParameterManager.setParameter(clParamLoop);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  82 */       if (isErrorDetected() == true)
/*     */       {
/*  85 */         for (String clParamName : this.m_clParamInvalidNameList) {
/*  86 */           Parameter clParam = getParameter(clParamName);
/*  87 */           if (clParam != null)
/*  88 */             clParam.setValid(Boolean.valueOf(false));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<String> getParamInvalidList()
/*     */   {
/* 101 */     return this.m_clParamInvalidNameList;
/*     */   }
/*     */ 
/*     */   public void clearParamInvalidList()
/*     */   {
/* 108 */     if (this.m_clParamInvalidNameList != null)
/* 109 */       this.m_clParamInvalidNameList.clear();
/*     */     else
/* 111 */       this.m_clParamInvalidNameList = new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean isErrorDetected()
/*     */   {
/* 121 */     boolean bReturn = false;
/*     */ 
/* 123 */     if (this.m_clParamInvalidNameList != null) {
/* 124 */       bReturn = !this.m_clParamInvalidNameList.isEmpty();
/*     */     }
/*     */ 
/* 127 */     return bReturn;
/*     */   }
/*     */ 
/*     */   public List<Parameter> verifyValuePrescaler(Component clParent, Parameter clSourceParameter, boolean bFirstVerify)
/*     */   {
/* 372 */     List clUpdateParam = null;
/* 373 */     int iTimeBit = 0; int iBitNbQuantum = 1; int iMinPrescaler = 0;
/* 374 */     double dIpClockFrequency = 0.0D;
/* 375 */     String sErrorSentence = "";
/*     */ 
/* 378 */     clearParamInvalidList();
/*     */ 
/* 380 */     Parameter parameter_BitSegment1 = getParameter("BS1");
/* 381 */     Parameter parameter_BitSegment2 = getParameter("BS2");
/*     */ 
/* 384 */     Parameter clParam = getParameter("BS1");
/*     */ 
/* 386 */     if (clParam != null)
/* 387 */       iBitNbQuantum += decodeBitSegment(clParam.getCurrentValueComment2());
/*     */     else {
/* 389 */       iBitNbQuantum++;
/*     */     }
/*     */ 
/* 392 */     clParam = getParameter("BS2");
/*     */ 
/* 394 */     if (clParam != null)
/* 395 */       iBitNbQuantum += decodeBitSegment(clParam.getCurrentValueComment2());
/*     */     else {
/* 397 */       iBitNbQuantum++;
/*     */     }
/*     */ 
/* 401 */     if (this.m_clRccService != null) {
/* 402 */       dIpClockFrequency = this.m_clRccService.getClockFrequency(this.m_clIp.getInstanceName());
/*     */     }
/*     */ 
/* 406 */     if (dIpClockFrequency > 0.0D)
/*     */     {
/* 408 */       double dIpClockTime = 1000000000.0D / dIpClockFrequency;
/*     */ 
/* 410 */       int iPrescaler = decodeString(clSourceParameter.getCurrentValue()).intValue();
/* 411 */       String iPrescalerNocheck = clSourceParameter.getCurrentValue();
/* 412 */       double iTimeQuantum = iPrescaler * dIpClockTime;
/* 413 */       double TimeBitTmp = iTimeQuantum * iBitNbQuantum;
/* 414 */       iTimeBit = (int)TimeBitTmp;
/*     */ 
/* 416 */       RefParameter sourceParameter = this.m_clIp.getParameter("Prescaler");
/* 417 */       RefParameter clRefParam = this.m_clIp.getRefParameter(clSourceParameter.getName());
/*     */ 
/* 420 */       iMinPrescaler = roundDouble(1000.0D / (iBitNbQuantum * dIpClockTime));
/*     */ 
/* 423 */       clParam = getParameter("CalculateTimeQuantum");
/* 424 */       Parameter ParamRemoveCheck = getParameter("Prescaler");
/* 425 */       if (clParam != null)
/*     */       {
/*     */         String sValue;
/*     */         String sValue;
/* 427 */         if (!ParamRemoveCheck.getHasCheck().booleanValue()) {
/* 428 */           sValue = iPrescalerNocheck + "*" + dIpClockTime;
/*     */         }
/*     */         else {
/* 431 */           sValue = String.valueOf(iTimeQuantum);
/*     */         }
/*     */ 
/* 435 */         if (!clParam.getCurrentValue().equals(sValue)) {
/* 436 */           clParam.setCurrentValue(sValue);
/* 437 */           if (clUpdateParam == null) {
/* 438 */             clUpdateParam = new ArrayList();
/*     */           }
/* 440 */           clUpdateParam.add(clParam);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 445 */       clParam = getParameter("CalculateTimeBit");
/* 446 */       if (clParam != null)
/*     */       {
/*     */         String sValue;
/*     */         String sValue;
/* 447 */         if (!ParamRemoveCheck.getHasCheck().booleanValue())
/* 448 */           sValue = iPrescalerNocheck + "*" + dIpClockTime + "*" + iBitNbQuantum;
/*     */         else {
/* 450 */           sValue = String.valueOf(iTimeBit);
/*     */         }
/*     */ 
/* 454 */         if (!clParam.getCurrentValue().equals(sValue)) {
/* 455 */           clParam.setCurrentValue(sValue);
/* 456 */           if (clUpdateParam == null) {
/* 457 */             clUpdateParam = new ArrayList();
/*     */           }
/* 459 */           clUpdateParam.add(clParam);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 465 */     if (iTimeBit < 1000)
/*     */     {
/* 467 */       parameter_BitSegment1.setCurrentNonValidValueComment(parameter_BitSegment1.getCurrentValueComment());
/* 468 */       parameter_BitSegment1.setValid(Boolean.valueOf(false));
/*     */ 
/* 470 */       parameter_BitSegment2.setCurrentNonValidValueComment(parameter_BitSegment2.getCurrentValueComment());
/* 471 */       parameter_BitSegment2.setValid(Boolean.valueOf(false));
/*     */ 
/* 473 */       this.m_clParamInvalidNameList.add(parameter_BitSegment1.getName());
/* 474 */       this.m_clParamInvalidNameList.add(parameter_BitSegment2.getName());
/* 475 */       if (clUpdateParam == null) {
/* 476 */         clUpdateParam = new ArrayList();
/*     */       }
/* 478 */       clUpdateParam.add(parameter_BitSegment1);
/* 479 */       clUpdateParam.add(parameter_BitSegment2);
/*     */     }
/*     */ 
/* 483 */     return clUpdateParam;
/*     */   }
/*     */ 
/*     */   public List<Parameter> verifyValueBitSegment(Component clParent, Parameter clSourceParameter)
/*     */   {
/* 498 */     List clUpdateParam = null;
/*     */ 
/* 500 */     int iBitNbQuantum = 1;
/*     */ 
/* 502 */     double dIpClockFrequency = 0.0D;
/* 503 */     String sErrorSentence = "";
/*     */ 
/* 506 */     if (this.m_clRccService != null) {
/* 507 */       dIpClockFrequency = this.m_clRccService.getClockFrequency(this.m_clIp.getInstanceName());
/*     */     }
/*     */ 
/* 511 */     if (dIpClockFrequency > 0.0D)
/*     */     {
/* 513 */       Parameter clParameter = getParameter("Prescaler");
/* 514 */       if (clParameter != null) {
/* 515 */         int iPrescaler = decodeString(clParameter.getCurrentValue()).intValue();
/*     */ 
/* 518 */         clParameter = getParameter("CalculateTimeQuantum");
/* 519 */         if (clParameter != null)
/*     */         {
/* 521 */           String sBitSegmentName = clSourceParameter.getName();
/*     */           int iLastNbBitQuantum;
/*     */           int iBitSegment;
/* 522 */           if (sBitSegmentName.equals("BS1") == true) {
/* 523 */             clParameter = getParameter("BS2");
/* 524 */             if (clParameter != null) {
/* 525 */               iBitNbQuantum += decodeBitSegment(clParameter.getCurrentValueComment2());
/*     */             }
/* 527 */             int iLastNbBitQuantum = iBitNbQuantum;
/*     */ 
/* 529 */             int iBitSegment = decodeBitSegment(clSourceParameter.getCurrentValueComment2());
/* 530 */             iBitNbQuantum += iBitSegment;
/*     */           } else {
/* 532 */             clParameter = getParameter("BS1");
/* 533 */             if (clParameter != null) {
/* 534 */               iBitNbQuantum += decodeBitSegment(clParameter.getCurrentValueComment2());
/*     */             }
/* 536 */             iLastNbBitQuantum = iBitNbQuantum;
/*     */ 
/* 538 */             iBitSegment = decodeBitSegment(clSourceParameter.getCurrentValueComment2());
/* 539 */             iBitNbQuantum += iBitSegment;
/*     */           }
/*     */ 
/* 543 */           double dIpClockTime = 1000000000.0D / dIpClockFrequency;
/* 544 */           double iTimeQuantum = iPrescaler * dIpClockTime;
/*     */ 
/* 546 */           double TimeBitTmp = iTimeQuantum * iBitNbQuantum;
/* 547 */           int iTimeBit = (int)TimeBitTmp;
/* 548 */           if (iTimeBit < 1000)
/*     */           {
/* 550 */             iBitNbQuantum = roundDouble(1000.0D / iTimeQuantum);
/* 551 */             int iMinBitSegment = iBitNbQuantum - iLastNbBitQuantum;
/* 552 */             sBitSegmentName = "BitSegment (" + sBitSegmentName + ")";
/* 553 */             if (((((sBitSegmentName.equalsIgnoreCase("BitSegment (BS1)")) && (iMinBitSegment > 16)) | sBitSegmentName.equalsIgnoreCase("BitSegment (BS2)"))) && (iMinBitSegment > 8)) {
/* 554 */               sErrorSentence = "With this Prescaler value, " + this.m_clRccService.getClockName(this.m_clIpInstanceName) + " Clock Frequency (" + valueHelpString((int)dIpClockFrequency, "Hz") + "), and this " + sBitSegmentName + " length (" + iBitSegment + " Tq), Nominal Bit Time is too short (" + iTimeBit + "nS).\nThe " + sBitSegmentName + " must be more than " + iMinBitSegment + ".\nPlease set Prescaler more than " + valueHelpString(iPrescaler, "");
/*     */             }
/*     */             else
/*     */             {
/* 560 */               sErrorSentence = "With this Prescaler value, " + this.m_clRccService.getClockName(this.m_clIpInstanceName) + " Clock Frequency (" + valueHelpString((int)dIpClockFrequency, "Hz") + "), and this " + sBitSegmentName + " length (" + iBitSegment + " Tq), Nominal Bit Time is too short (" + iTimeBit + "nS).\nPlease set " + sBitSegmentName + " more than " + iMinBitSegment + ".\nPlease set Prescaler more than " + valueHelpString(iPrescaler, "");
/*     */             }
/*     */ 
/* 567 */             JOptionPane.showMessageDialog(clParent, sErrorSentence, "Parameter Error", 0);
/*     */ 
/* 574 */             iBitNbQuantum = iLastNbBitQuantum + decodeBitSegment(clSourceParameter.getPreviousValueComment());
/* 575 */             clSourceParameter.setCurrentValue(clSourceParameter.getPreviousValue());
/* 576 */             clUpdateParam = new ArrayList();
/* 577 */             clUpdateParam.add(clSourceParameter);
/*     */           } else {
/* 579 */             Parameter ParameterBS1 = getParameter("BS1");
/* 580 */             Parameter ParameterBS2 = getParameter("BS2");
/* 581 */             if (clUpdateParam == null) {
/* 582 */               clUpdateParam = new ArrayList();
/*     */             }
/* 584 */             this.m_clParamInvalidNameList.remove("BS1");
/* 585 */             this.m_clParamInvalidNameList.remove("BS2");
/* 586 */             ParameterBS1.setValid(Boolean.valueOf(true));
/* 587 */             ParameterBS2.setValid(Boolean.valueOf(true));
/*     */ 
/* 589 */             clUpdateParam.add(ParameterBS1);
/* 590 */             clUpdateParam.add(ParameterBS2);
/*     */           }
/*     */ 
/* 594 */           clParameter = getParameter("CalculateTimeBit");
/* 595 */           Parameter paramRemoveCheck = getParameter("Prescaler");
/* 596 */           String iPrescalerNocheck = clSourceParameter.getCurrentValue();
/*     */ 
/* 598 */           if (clParameter != null) {
/* 599 */             TimeBitTmp = iTimeQuantum * iBitNbQuantum;
/* 600 */             iTimeBit = (int)TimeBitTmp;
/*     */             String sValue;
/*     */             String sValue;
/* 601 */             if (!paramRemoveCheck.getHasCheck().booleanValue())
/*     */             {
/* 603 */               sValue = iTimeQuantum + "*" + iBitNbQuantum;
/*     */             }
/*     */             else {
/* 606 */               sValue = String.valueOf(iTimeBit);
/*     */             }
/*     */ 
/* 609 */             if (!clParameter.getCurrentValue().equals(sValue)) {
/* 610 */               clParameter.setCurrentValue(sValue);
/* 611 */               if (clUpdateParam == null) {
/* 612 */                 clUpdateParam = new ArrayList();
/*     */               }
/* 614 */               clUpdateParam.add(clParameter);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 621 */     return clUpdateParam;
/*     */   }
/*     */ 
/*     */   public void setIdFilter(Parameter clSourceParameter)
/*     */   {
/* 635 */     long lIdFilter = decodeString(clSourceParameter.getCurrentValue()).longValue();
/* 636 */     int iIdLowFilter = (int)(lIdFilter & 0xFFFF);
/* 637 */     int iIdHighFilter = (int)(lIdFilter >> 16 & 0xFFFF);
/*     */ 
/* 640 */     RefParameter clRefParamHigh = this.m_clIp.getRefParameter("FilterIdHigh");
/* 641 */     if (clRefParamHigh != null) {
/* 642 */       RefParameter clRefParamLow = this.m_clIp.getRefParameter("FilterIdLow");
/* 643 */       if (clRefParamLow != null) {
/* 644 */         this.m_clIp.setParameterDisplayValue(clRefParamHigh.getName(), clRefParamHigh.getId(), String.valueOf(iIdHighFilter));
/* 645 */         this.m_clIp.setParameterDisplayValue(clRefParamLow.getName(), clRefParamLow.getId(), String.valueOf(iIdLowFilter));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMaskFilter(Parameter clSourceParameter)
/*     */   {
/* 661 */     long lMaskFilter = decodeString(clSourceParameter.getCurrentValue()).longValue();
/* 662 */     int iMaskLowFilter = (int)(lMaskFilter & 0xFFFF);
/* 663 */     int iMaskHighFilter = (int)(lMaskFilter >> 16 & 0xFFFF);
/*     */ 
/* 666 */     RefParameter clRefParamHigh = this.m_clIp.getRefParameter("FilterMaskIdHigh");
/* 667 */     if (clRefParamHigh != null) {
/* 668 */       RefParameter clRefParamLow = this.m_clIp.getRefParameter("FilterMaskIdLow");
/* 669 */       if (clRefParamLow != null) {
/* 670 */         this.m_clIp.setParameterDisplayValue(clRefParamHigh.getName(), clRefParamHigh.getId(), String.valueOf(iMaskHighFilter));
/* 671 */         this.m_clIp.setParameterDisplayValue(clRefParamLow.getName(), clRefParamLow.getId(), String.valueOf(iMaskLowFilter));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Parameter getParameter(String sParameterName)
/*     */   {
/* 684 */     Parameter clParam = null;
/* 685 */     int iIndex = 0;
/*     */ 
/* 687 */     List clParamList = this.m_clParameterManager.getIpParameters();
/* 688 */     while ((iIndex < clParamList.size()) && (clParam == null)) {
/* 689 */       if (((Parameter)clParamList.get(iIndex)).getName().equals(sParameterName) == true) {
/* 690 */         clParam = (Parameter)clParamList.get(iIndex);
/*     */       }
/* 692 */       iIndex++;
/*     */     }
/*     */ 
/* 695 */     return clParam;
/*     */   }
/*     */ 
/*     */   private Long decodeString(String sValue)
/*     */   {
/* 705 */     Long clValue = null;
/*     */     try
/*     */     {
/* 708 */       clValue = Long.decode(sValue);
/*     */     } catch (NumberFormatException clException) {
/* 710 */       clValue = new Long(0L);
/*     */     }
/*     */ 
/* 713 */     return clValue;
/*     */   }
/*     */ 
/*     */   private int decodeBitSegment(String sValue)
/*     */   {
/* 723 */     Integer clValue = new Integer(0);
/*     */ 
/* 727 */     int iIndex = sValue.indexOf(" ");
/* 728 */     if (iIndex != -1)
/*     */       try {
/* 730 */         clValue = Integer.decode(sValue.substring(0, iIndex));
/*     */       }
/*     */       catch (NumberFormatException clException)
/*     */       {
/*     */       }
/* 735 */     return clValue.intValue();
/*     */   }
/*     */ 
/*     */   private String valueHelpString(int iValue, String sUnit)
/*     */   {
/* 748 */     String sFinalText = String.valueOf(iValue);
/*     */ 
/* 751 */     if (sUnit == null) {
/* 752 */       sUnit = "";
/*     */     }
/*     */ 
/* 756 */     if (!sUnit.isEmpty()) {
/* 757 */       sFinalText = sFinalText + " " + sUnit;
/*     */ 
/* 760 */       float fValue = iValue;
/* 761 */       if (fValue > 1000000.0D) {
/* 762 */         fValue = (float)(fValue / 1000000.0D);
/* 763 */         sFinalText = fValue + " M" + sUnit;
/*     */       }
/* 765 */       else if (fValue > 1000.0D) {
/* 766 */         fValue = (float)(fValue / 1000.0D);
/* 767 */         sFinalText = fValue + " K" + sUnit;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 772 */     return sFinalText;
/*     */   }
/*     */ 
/*     */   private int roundDouble(double dValue)
/*     */   {
/* 783 */     int iValue = (int)dValue;
/* 784 */     if (iValue != dValue) {
/* 785 */       iValue++;
/*     */     }
/*     */ 
/* 788 */     return iValue;
/*     */   }
/*     */ }

/* Location:           /home/fosterb/projects/fix_can/can.jar
 * Qualified Name:     com.st.microxplorer.plugins.ip.can.gui.CanManager
 * JD-Core Version:    0.6.2
 */