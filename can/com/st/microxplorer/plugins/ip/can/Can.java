/*     */ package com.st.microxplorer.plugins.ip.can;
/*     */ 
/*     */ import com.st.microxplorer.maingui.MicroXplorer;
/*     */ import com.st.microxplorer.mcu.IP;
/*     */ import com.st.microxplorer.mcu.Mcu;
/*     */ import com.st.microxplorer.mxsystem.MxSystem;
/*     */ import com.st.microxplorer.plugins.ip.can.gui.CanManager;
/*     */ import com.st.microxplorer.plugins.ip.can.gui.CanParametersView;
/*     */ import com.st.microxplorer.plugins.ipmanager.generictreatment.gui.MainPanel;
/*     */ import com.st.microxplorer.plugins.ipmanager.generictreatment.model.ParamManager;
/*     */ import com.st.microxplorer.plugins.ipmanager.pluginmanagement.ButtonStateConstants;
/*     */ import com.st.microxplorer.plugins.ipmanager.pluginmanagement.IPUIPlugin;
/*     */ import com.st.microxplorer.plugins.ipmanager.pluginmanagement.IpPluginManagement;
/*     */ import com.st.microxplorer.util.GuiChangeEvents;
/*     */ import java.awt.Component;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.util.List;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ public class Can extends IPUIPlugin
/*     */   implements ButtonStateConstants, PropertyChangeListener, GuiChangeEvents
/*     */ {
/*  29 */   static final Logger log = Logger.getLogger(Can.class.getName());
/*  30 */   private MainPanel m_clMainPanel = null;
/*  31 */   private ParamManager m_clParamManager = null;
/*     */   private CanManager m_clCanManager;
/*     */ 
/*     */   public Component getUI()
/*     */   {
/*  38 */     this.m_clMainPanel = new MainPanel(this, this.m_clParamManager, new CanParametersView(this.m_clParamManager, this.m_clCanManager));
/*  39 */     return this.m_clMainPanel;
/*     */   }
/*     */ 
/*     */   public void onDisable()
/*     */   {
/*  44 */     MxSystem.getMxSystem().removePropertyChangeListener(this);
/*     */   }
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*  50 */     if (this.m_clParamManager == null)
/*     */     {
/*  52 */       this.m_clParamManager = new ParamManager(this.ip);
/*  53 */       this.m_clCanManager = new CanManager(this.ip, this.m_clParamManager);
/*     */ 
/*  56 */       log.info("Can Add PropertyChangeListener " + toString());
/*  57 */       MxSystem.getMxSystem().addPropertyChangeListener(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void propertyChange(PropertyChangeEvent clEvent)
/*     */   {
/*  68 */     if ((this.m_clParamManager != null) && (
/*  69 */       ((clEvent.getSource() instanceof Mcu)) || ((clEvent.getSource() instanceof MxSystem))))
/*  70 */       switch (clEvent.getPropertyName()) {
/*     */       case "EventGuiUpdateRccFrequencies":
/*  72 */         this.m_clCanManager.clockChange();
/*  73 */         break;
/*     */       case "EventMcuChangeMode":
/*  76 */         String sOldValue = (String)clEvent.getOldValue();
/*  77 */         if (sOldValue.equals(this.ip.getInstanceName()) == true) {
/*  78 */           this.m_clParamManager.updateIpParameters();
/*  79 */           this.m_clCanManager.clearParamInvalidList();
/*  80 */           this.m_clCanManager.clockChange();
/*  81 */           super.updateDiagram();
/*     */         }
/*     */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  97 */     Class ipPluginClass = Can.class;
/*  98 */     String ipMatch = "CAN.*";
/*  99 */     IpPluginManagement.registerViewForDebug(ipPluginClass, ipMatch);
/*     */ 
/* 101 */     MicroXplorer.main(args);
/*     */   }
/*     */ 
/*     */   public String getConfigurationState()
/*     */   {
/* 107 */     if ((this.m_clCanManager != null) && (this.m_clCanManager.isErrorDetected() == true)) {
/* 108 */       return "Error";
/*     */     }
/* 110 */     return this.m_clParamManager.getConfigurationState();
/*     */   }
/*     */ 
/*     */   public List<String> getDependencyList()
/*     */   {
/* 116 */     List dependencies = this.m_clParamManager.fetchSharedComponentsDependency();
/* 117 */     return dependencies;
/*     */   }
/*     */ }

/* Location:           /home/fosterb/projects/fix_can/can.jar
 * Qualified Name:     com.st.microxplorer.plugins.ip.can.Can
 * JD-Core Version:    0.6.2
 */