package ca.gosyer.usbhandler

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.command.main
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import com.lordcodes.turtle.shellRun
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.codecrete.usb.Usb
import net.codecrete.usb.UsbDevice
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.io.path.absolutePathString
import kotlin.time.Duration.Companion.seconds

fun usbDeviceConnectedEvents() = callbackFlow<UsbDevice>{
    Usb.setOnDeviceConnected {
        trySend(it)
    }

    awaitClose {
        Usb.setOnDeviceConnected(null)
    }
}

fun usbDeviceDisconnectedEvents() = callbackFlow<UsbDevice>{
    Usb.setOnDeviceDisconnected {
        trySend(it)
    }

    awaitClose {
        Usb.setOnDeviceDisconnected(null)
    }
}

class Main : SuspendingCliktCommand() {
    val usbSerial by option(help="Serial number of the Monitor")
        .required()
    val mainMonitorId by option(help="Main Monitor ID as defined by MultiMonitorTool")
        .required()
    val secondaryMonitorId by option(help="Secondary Monitor ID as defined by MultiMonitorTool")
        .required()
    val multiMonitorTool by option(help="Path to MultiMonitorTool", envvar = "MULTI_MONITOR_TOOL")
        .path(mustExist = true, canBeDir = false)
        .required()

    @OptIn(ExperimentalStdlibApi::class, ExperimentalAtomicApi::class, DelicateCoroutinesApi::class)
    override suspend fun run() {
        val job = AtomicReference<Job?>(null)
        val monitorsChanged = AtomicBoolean(false)
        usbDeviceDisconnectedEvents()
            .onEach {
                if (it.serialNumber == usbSerial) {
                    job.exchange(null)?.cancel()
                    if (monitorsChanged.compareAndSet(expectedValue = true, newValue = false)) {
                        runMultiMonitor(mainMonitorId)
                    }
                }
            }
            .launchIn(GlobalScope)

        usbDeviceConnectedEvents()
            .mapLatest { usbDevice ->
                if (usbDevice.serialNumber == usbSerial) {
                    job.exchange(
                        GlobalScope.launch {
                            delay(7.seconds)
                            runMultiMonitor(secondaryMonitorId)
                            monitorsChanged.store(true)
                        }
                    )?.cancel()
                }
                println("UsbDevice connected! ${usbDevice.product} ${usbDevice.manufacturer} ${usbDevice.serialNumber}")
            }
            .collect()
    }

    fun runMultiMonitor(monitorId: String) {
        shellRun {
            command(
                multiMonitorTool.absolutePathString(),
                arguments = listOf(
                    "/SetPrimary",
                    monitorId,
                )
            )
        }
    }
}

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

suspend fun main(args: Array<String>) = Main().main(args)
