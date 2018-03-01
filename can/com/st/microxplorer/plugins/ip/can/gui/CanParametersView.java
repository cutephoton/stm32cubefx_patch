/*     */ package com.st.microxplorer.plugins.ip.can.gui;
/*     */ 
/*     */ import com.st.microxplorer.plugins.ipmanager.generictreatment.gui.IpParametersView;
/*     */ import com.st.microxplorer.plugins.ipmanager.generictreatment.model.ParamManager;
/*     */ import com.st.microxplorer.plugins.ipmanager.generictreatment.model.Parameter;
/*     */ import com.st.microxplorer.plugins.ipmanager.generictreatment.model.ParameterProperty;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class CanParametersView extends IpParametersView
/*     */   implements CanConstants
/*     */ {
/*     */   private static final long serialVersionUID = -8484660612570171859L;
/*  26 */   private CanManager m_clCanManager = null;
/*  27 */   private ParamManager m_clParameterManager = null;
/*     */   private List<String> m_clParamPropertyNameList;
/*  29 */   private boolean m_bLock = false;
/*     */ 
/*     */   public CanParametersView(ParamManager clParameterManager, CanManager clCanManager)
/*     */   {
/*  39 */     super(clParameterManager);
/*     */ 
/*  42 */     this.m_clParameterManager = clParameterManager;
/*  43 */     if (clParameterManager.getIpParameters().size() != 0)
/*     */     {
/*  45 */       this.m_clCanManager = clCanManager;
/*  46 */       this.m_clParamPropertyNameList = new ArrayList();
/*     */ 
/*  49 */       Parameter clParameter = getParameter("Prescaler");
/*  50 */       if (clParameter != null)
/*     */       {
/*  52 */         List clUpdateParamList = this.m_clCanManager.verifyValuePrescaler(this, clParameter, true);
/*  53 */         updateParameters(true, clParameter, clUpdateParamList);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected IpParametersView clone()
/*     */   {
/*  63 */     return new CanParametersView(this.m_clParameterManager, this.m_clCanManager);
/*     */   }
/*     */ 
/*     */   public void verifyFieldAction(ParameterProperty clParameterProperty)
/*     */   {
/*  74 */     List clUpdateParamList = null;
/*     */ 
/*  76 */     super.verifyFieldAction(clParameterProperty);
/*     */ 
/*  79 */     this.m_bLock = true;
/*  80 */     if (this.m_clCanManager != null)
/*     */     {
/*  82 */       Parameter clSourceParameter = clParameterProperty.getCorrespondingParameter();
/*  83 */       if (clSourceParameter != null)
/*     */       {
/*  85 */         switch (clSourceParameter.getName()) {
/*     */         case "Prescaler":
/*  87 */           clUpdateParamList = this.m_clCanManager.verifyValuePrescaler(this, clSourceParameter, false);
/*  88 */           break;
/*     */         case "BS1":
/*     */         case "BS2":
/*  94 */           clUpdateParamList = this.m_clCanManager.verifyValueBitSegment(this, clSourceParameter);
/*  95 */           break;
/*     */         case "FilterId32b":
/*  97 */           this.m_clCanManager.setIdFilter(clSourceParameter);
/*  98 */           break;
/*     */         case "FilterMaskId32b":
/* 100 */           this.m_clCanManager.setMaskFilter(clSourceParameter);
/*     */         }
/*     */ 
/* 105 */         updateParameters(false, clSourceParameter, clUpdateParamList);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 110 */     this.m_bLock = false;
/*     */   }
/*     */ 
/*     */   public boolean restoreDefaultValues()
/*     */   {
/* 121 */     this.m_clParameterManager.restoreDefaultValuesParameters();
/* 122 */     this.m_clParameterManager.updateIpParameters();
/* 123 */     this.m_clCanManager.clearParamInvalidList();
/* 124 */     this.m_clCanManager.clockChange();
/* 125 */     rebuild();
/* 126 */     return true;
/*     */   }
/*     */ 
/*     */   public void apply()
/*     */   {
/* 135 */     if (!this.m_bLock)
/* 136 */       super.apply();
/*     */   }
/*     */ 
/*     */   public boolean save()
/*     */   {
/* 146 */     if (!this.m_bLock) {
/* 147 */       return super.save();
/*     */     }
/* 149 */     return false;
/*     */   }
/*     */ 
/*     */   protected void doSpecificStaffOnPropertiesBeforePopulating(List<ParameterProperty> clParamPropertyList)
/*     */   {
/* 162 */     if (this.m_clParamPropertyNameList != null)
/*     */     {
/* 164 */       for (String clParamPropertyName : this.m_clParamPropertyNameList)
/*     */       {
/* 166 */         ParameterProperty clParamProperty = getParamProperty(clParamPropertyList, clParamPropertyName);
/* 167 */         if (clParamProperty != null) {
/* 168 */           clParamProperty.setChangedDueToDependency(true);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 173 */     if ((this.m_clCanManager != null) && (this.m_clCanManager.isErrorDetected() == true))
/*     */     {
/* 175 */       for (String clParamName : this.m_clCanManager.getParamInvalidList())
/*     */       {
/* 177 */         Parameter clParam = getParameter(clParamName);
/* 178 */         if (clParam != null)
/* 179 */           clParam.setValid(Boolean.valueOf(false));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updateParameters(boolean bFirst, Parameter clSourceParameter, List<Parameter> clUpdateParamList)
/*     */   {
/* 193 */     ParameterProperty clParameterProperty = null;
/*     */ 
/* 196 */     this.m_clParamPropertyNameList.clear();
/*     */ 
/* 199 */     if (clUpdateParamList != null)
/*     */     {
/* 201 */       for (Parameter clParam : clUpdateParamList)
/*     */       {
/* 203 */         this.paramManager.setParameter(clParam);
/*     */ 
/* 206 */         if ((!bFirst) && (clParam != clSourceParameter))
/*     */         {
/* 208 */           clParameterProperty = getProperty(clParam);
/* 209 */           if (clParameterProperty != null)
/* 210 */             this.m_clParamPropertyNameList.add(clParameterProperty.getName());
/*     */         }
/*     */       }
/*     */     }
/* 214 */     rebuild();
/*     */   }
/*     */ 
/*     */   private ParameterProperty getParamProperty(List<ParameterProperty> clParamPropertyList, String sName)
/*     */   {
/* 226 */     ParameterProperty clParamProperty = null;
/* 227 */     boolean bFound = false;
/* 228 */     int iIndex = 0;
/*     */ 
/* 230 */     while ((iIndex < clParamPropertyList.size()) && (!bFound))
/*     */     {
/* 232 */       if (sName.equals(((ParameterProperty)clParamPropertyList.get(iIndex)).getName()) == true)
/*     */       {
/* 234 */         clParamProperty = (ParameterProperty)clParamPropertyList.get(iIndex);
/* 235 */         bFound = true;
/*     */       }
/* 237 */       iIndex++;
/*     */     }
/*     */ 
/* 240 */     return clParamProperty;
/*     */   }
/*     */ }

/* Location:           /home/fosterb/projects/fix_can/can.jar
 * Qualified Name:     com.st.microxplorer.plugins.ip.can.gui.CanParametersView
 * JD-Core Version:    0.6.2
 */