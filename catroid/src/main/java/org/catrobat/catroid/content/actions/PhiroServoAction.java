/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick.Motor;
import org.catrobat.catroid.content.bricks.PhiroServoBrick;
import org.catrobat.catroid.devices.arduino.phiro.Phiro;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class PhiroServoAction extends TemporalAction {
	private static final int MIN_SPEED = 0;
	private static final int MAX_SPEED = 100;

	private PhiroServoBrick.Servo motorEnum;
	private Formula speed;
	private Sprite sprite;

	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void update(float percent) {
		int angleValue;
		try {
			angleValue = speed.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			angleValue = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		if (angleValue < MIN_SPEED) {
			angleValue = MIN_SPEED;
		} else if (angleValue > MAX_SPEED) {
			angleValue = MAX_SPEED;
		}

		Phiro phiro = btService.getDevice(BluetoothDevice.PHIRO);
		if (phiro == null) {
			return;
		}

		switch (motorEnum) {
			case SERVO_LEFT:
				phiro.setLeftServoPosition(angleValue);
				break;
			case SERVO_RIGHT:
				phiro.setRightServoPosition(angleValue);
				break;
			case SERVO_BOTH:
				phiro.setLeftServoPosition(angleValue);
				phiro.setRightServoPosition(angleValue);
				break;
		}
	}

	public void setServoEnum(PhiroServoBrick.Servo motorEnum) {
		this.motorEnum = motorEnum;
	}

	public void setSpeed(Formula speed) {
		this.speed = speed;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
