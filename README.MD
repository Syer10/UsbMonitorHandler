USB Monitor Handler
=======

## Overview

__USB Monitor Handler__ is a small program which controls the primary monitor based on USB events, if it detects a certain USB SERIAL being activated, it sets the primary monitor to it using [MultiMonitorTool](https://www.nirsoft.net/utils/multi_monitor_tool.html). This requires that the monitor includes a KVM hub that is connected to the computer.

I needed an application like this since I primarily game on my OLED monitor, but if I leave it on while I am doing desktop work, I risk burn-in. Many games also rely on the primary monitor configuration, so I have to switch the primary monitor whenever I start and stop gaming.

Building this was an idea I had while switching my primary monitor for a gaming session one night, I wanted something automatic, so I decided to build it myself.

Currently, it is limited to running through java, but I hope to get native executables soon using GraalVM.


### Example
In the following example we are listening for activation of the USB serial 1111111111, which is the KVM hub of the monitor. Our normal primary monitor is DELD0G5, and our gaming monitor is DEL27Y8. The program also needs a path to your downloaded [MultiMonitorTool](https://www.nirsoft.net/utils/multi_monitor_tool.html).
```bash
java -jar UsbMonitorHandler.jar --usb-serial 1111111111 --main-monitor-id DELD0G5 --secondary-monitor-id DEL27Y8 --multi-monitor-tool "D:\Programs\MultiMonitorTool.exe"
```

To get the usb-serial you need to run the application with a fake serial number. When you turn on your monitor watch the logs for your monitor activation, the last set of numbers during a line is the serial number you need.

Getting monitor IDs is easy, just launch [MultiMonitorTool](https://www.nirsoft.net/utils/multi_monitor_tool.html), and you can use the short monitor id, or if you need something more specific look at the MultiMonitorTool website and search `<Monitor>` for how to get longer or more specific monitor ids.


## License

    Copyright (C) 2025 Mitchell Syer

    This Source Code Form is subject to the terms of the Mozilla Public
    License, v. 2.0. If a copy of the MPL was not distributed with this
    file, You can obtain one at http://mozilla.org/MPL/2.0/.