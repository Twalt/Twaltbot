/**
 * Tester.java
 * Tests the Lamp Security Program using Jmock.
 * @author kahlya
 * @author shankera
 */
package Test;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import SecurityLightController.*;

import junit.framework.TestCase;

/**
 * Uses Jmock to test the LightControllerStateMachine and therefore the lamp
 * security program.
 * 
 * @author kahlya
 * @author shankera
 * @version 1.0 - May 9th, 2013
 */
public class Tester extends TestCase {

	private Mockery context = new Mockery();
	private LightControllerStateMachine classUnderTest;
	private LightControllerCommandInterface mockCommand;
	private LightTimerInterface mockTimer;
	private LightDeviceInterface mockInterface;
	private LightControllerStateMachineObserverInterface lightObserver;

	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		mockCommand = context.mock(LightControllerCommandInterface.class);
		mockTimer = context.mock(LightTimerInterface.class);
		mockInterface = context.mock(LightDeviceInterface.class);
		lightObserver = context
				.mock(LightControllerStateMachineObserverInterface.class);

		classUnderTest = new LightControllerStateMachine();
		classUnderTest.setLight(mockInterface);
		classUnderTest.setTmr(mockTimer);
		classUnderTest.subscribe(lightObserver);
	}

	@Test
	// Tests the Lamp Off with Daylight.
	public void testLampOffDaylight() {
		context.checking(new Expectations() {
			{
				// Moves to Lamp On Full
				oneOf(mockInterface).turnLightOnFullBrightness();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_ON_FULL_BRIGHTNESS);

				// Moves to Lamp Off Daylight
				oneOf(mockInterface).turnLightOff();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_OFF_DAYLIGHT);

				// Moves to Lamp Off Nighttime
				oneOf(mockInterface).turnLightOff();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_OFF_NIGHTIME);

				// Moves to Lamp Off Daylight
				oneOf(mockInterface).turnLightOff();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_OFF_DAYLIGHT);

			}
		});

		// Moves to Lamp on full
		classUnderTest
				.signalAction(LightControllerStateMachine.MANUAL_SWITCH_ON);
		// Moves to lamp off daylight
		classUnderTest
				.signalAction(LightControllerStateMachine.MANUAL_SWITCH_OFF);
		// Moves to Lamp off nighttime
		classUnderTest
				.signalAction(LightControllerStateMachine.LIGHT_SENSOR_DARKENED);
		// Moves to Lamp off daylight
		classUnderTest
				.signalAction(LightControllerStateMachine.LIGHT_SENSOR_LIGHTENED);
		// Tries to move to motion detected. Does nothing.
		classUnderTest
				.signalAction(LightControllerStateMachine.MOTION_DETECTED);
		// Tries to move to Intrusion Detected. Does nothing.
		classUnderTest
				.signalAction(LightControllerStateMachine.SECURITY_ALARM_TRIPPED);

		context.assertIsSatisfied();
	}

	@Test
	// Test lamp off Nighttime that was not tested in testLampOffDaylight above.
	public void testLampOffNighttime() {
		context.checking(new Expectations() {
			{
				// Move to Lamp off night time
				oneOf(mockInterface).turnLightOff();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_OFF_NIGHTIME);

				// Moves to Lamp on nighttime brightness
				oneOf(mockInterface).turnLightOnNightimeBrightness();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_ON_NIGHTIME_BRIGHTNESS);

				// Moves to lamp off nighttime
				oneOf(mockInterface).turnLightOff();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_OFF_NIGHTIME);

			}
		});

		// Moves to lamp off night time
		classUnderTest
				.signalAction(LightControllerStateMachine.LIGHT_SENSOR_DARKENED);
		// Moves to lamp on nighttime brightness
		classUnderTest
				.signalAction(LightControllerStateMachine.MANUAL_SWITCH_ON);
		// moves to lamp off nighttime.
		classUnderTest
				.signalAction(LightControllerStateMachine.MANUAL_SWITCH_OFF);

		// No additional tests were constructed because this state can transfer
		// using all other possible commands correctly.
	}

	@Test
	// Tests Lamp on Nighttime brightness that was not been tested above.
	public void testLampOnNighttime() {
		context.checking(new Expectations() {
			{
				// Move to lamp off nighttime
				oneOf(mockInterface).turnLightOff();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_OFF_NIGHTIME);

				// Move to lamp on night time brightness
				oneOf(mockInterface).turnLightOnNightimeBrightness();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_ON_NIGHTIME_BRIGHTNESS);

				// Move to lamp on full
				oneOf(mockInterface).turnLightOnFullBrightness();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_ON_FULL_BRIGHTNESS);

				// Move to lamp on nighttime brightness
				oneOf(mockInterface).turnLightOnNightimeBrightness();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_ON_NIGHTIME_BRIGHTNESS);

			}
		});

		// Move to lamp off nighttime
		classUnderTest
				.signalAction(LightControllerStateMachine.LIGHT_SENSOR_DARKENED);
		// Move to lamp on nighttime brightness
		classUnderTest
				.signalAction(LightControllerStateMachine.MANUAL_SWITCH_ON);
		// Move to lamp on full
		classUnderTest
				.signalAction(LightControllerStateMachine.LIGHT_SENSOR_LIGHTENED);
		// Move to lamp on nighttime brightness
		classUnderTest
				.signalAction(LightControllerStateMachine.LIGHT_SENSOR_DARKENED);
		// Tries to move to motion detected. Nothing happens.
		classUnderTest
				.signalAction(LightControllerStateMachine.MOTION_DETECTED);
		// Tries to move to Intrusion Detected. Nothing happens.
		classUnderTest
				.signalAction(LightControllerStateMachine.SECURITY_ALARM_TRIPPED);

	}

	@Test
	// Tests Lamp On Full for what has not been tested above.
	public void testLampOnFull() {
		context.checking(new Expectations() {
			{
				// Moves to Lamp on Full.
				oneOf(mockInterface).turnLightOnFullBrightness();
				oneOf(lightObserver)
						.updateLightState(
								LightControllerStateMachineObserverInterface.LAMP_ON_FULL_BRIGHTNESS);

			}
		});

		// Moves to lamp on full.
		classUnderTest
				.signalAction(LightControllerStateMachine.MANUAL_SWITCH_ON);
		// Tries to move to Motion detected, nothing happens.
		classUnderTest
				.signalAction(LightControllerStateMachine.MOTION_DETECTED);
		// Tries to move to Intrusion Detected. Nothing happens.
		classUnderTest
				.signalAction(LightControllerStateMachine.SECURITY_ALARM_TRIPPED);
	}
}
