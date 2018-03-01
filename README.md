# STM32CubeMX Unofficial Bugfix/Patching

As nice as STM32CubeMX can be for generating and updating STM32X projects
it has a number of bugs. I got annoyed enough to fix one.

This repository was created primarily so I could send ST a bug report.
As well, to help anybody who encounters an issue in the mean time.

## CAN

See https://github.com/cutephoton/stm32cubemx_patch/commit/58a36ace199579b30e658c3b4b65f32439132863 for fix.

### Symptom

```
Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException
    at com.st.microxplorer.plugins.ip.can.gui.CanManager.verifyValuePrescaler(CanManager.java:189)
    at com.st.microxplorer.plugins.ip.can.gui.CanManager.clockChange(CanManager.java:51)
    at com.st.microxplorer.plugins.ip.can.gui.CanManager.<init>(CanManager.java:41)
    at com.st.microxplorer.plugins.ip.can.Can.onEnable(Can.java:45)
    at com.st.microxplorer.plugins.ipmanager.pluginmanagement.IPUIPlugin.onEnablePlugin(IPUIPlugin.java:256)
    at com.st.microxplorer.plugins.ipmanager.gui.BlockDiagram.enableDisableButtons(BlockDiagram.java:1065)
    at com.st.microxplorer.plugins.ipmanager.gui.BlockDiagram.createPanels(BlockDiagram.java:439)
    at com.st.microxplorer.plugins.ipmanager.gui.BlockDiagram.propertyChange(BlockDiagram.java:974)
    at java.beans.PropertyChangeSupport.fire(PropertyChangeSupport.java:335)
    at java.beans.PropertyChangeSupport.firePropertyChange(PropertyChangeSupport.java:327)
    at java.beans.PropertyChangeSupport.firePropertyChange(PropertyChangeSupport.java:263)
    at com.st.microxplorer.util.MXPropertyChangeSupport.firePropertyChange(MXPropertyChangeSupport.java:40)
    at com.st.microxplorer.mxsystem.MxSystem.completeLoadConfig(MxSystem.java:369)
    at com.st.microxplorer.plugins.filemanager.engine.OpenFileManager.LoadConfig(OpenFileManager.java:269)
    at com.st.microxplorer.plugins.filemanager.engine.OpenFileManager.loadConfigurationFile(OpenFileManager.java:195)
    at com.st.microxplorer.plugins.filemanager.engine.OpenFileManager.userChoiceLoadConfig(OpenFileManager.java:97)
    at com.st.microxplorer.plugins.filemanager.engine.MainFileManager.userChoiceAndLoadConfig(MainFileManager.java:164)
    at com.st.microxplorer.plugins.filemanager.FileManagerView$2.actionPerformed(FileManagerView.java:409)
    at javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)
    at javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348)
    at javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402)
    at javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259)
    at javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252)
    at java.awt.AWTEventMulticaster.mouseReleased(AWTEventMulticaster.java:289)
    at java.awt.Component.processMouseEvent(Component.java:6533)
    at javax.swing.JComponent.processMouseEvent(JComponent.java:3324)
    at java.awt.Component.processEvent(Component.java:6298)
    at java.awt.Container.processEvent(Container.java:2236)
    at java.awt.Component.dispatchEventImpl(Component.java:4889)
    at java.awt.Container.dispatchEventImpl(Container.java:2294)
    at java.awt.Component.dispatchEvent(Component.java:4711)
    at java.awt.LightweightDispatcher.retargetMouseEvent(Container.java:4888)
    at java.awt.LightweightDispatcher.processMouseEvent(Container.java:4525)
    at java.awt.LightweightDispatcher.dispatchEvent(Container.java:4466)
    at java.awt.Container.dispatchEventImpl(Container.java:2280)
    at java.awt.Window.dispatchEventImpl(Window.java:2746)
    at java.awt.Component.dispatchEvent(Component.java:4711)
    at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:758)
    at java.awt.EventQueue.access$500(EventQueue.java:97)
    at java.awt.EventQueue$3.run(EventQueue.java:709)
    at java.awt.EventQueue$3.run(EventQueue.java:703)
    at java.security.AccessController.doPrivileged(Native Method)
    at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:80)
    at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:90)
    at java.awt.EventQueue$4.run(EventQueue.java:731)
    at java.awt.EventQueue$4.run(EventQueue.java:729)
    at java.security.AccessController.doPrivileged(Native Method)
    at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:80)
    at java.awt.EventQueue.dispatchEvent(EventQueue.java:728)
    at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:201)
    at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:116)
    at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:105)
    at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
    at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:93)
    at java.awt.EventDispatchThread.run(EventDispatchThread.java:82)
```

### Cause and Solution

The CAN manager has a typo for a constant. This is reflected in the CanConstants.java file.

```
  public static final String Parameter_BitSegment1Cte = "BS1";
  public static final String Parameter_BitSegment2Cte = "BS2";
```

However it should really be:

```
  public static final String Parameter_BitSegment1Cte = "TimeSeg1";
  public static final String Parameter_BitSegment2Cte = "TimeSeg2";
```

I decompiled the class, rebuilt it using the proper constants. Since it was a decompiled
class the references to CanConstants were lost so it ended up being a find/replace job.

Copy can/can.jar to STM32CubeMX/plugins/ip to fix the CAN prescaler exception.
