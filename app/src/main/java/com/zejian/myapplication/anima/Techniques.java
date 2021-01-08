
/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 daimajia
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.zejian.myapplication.anima;


import com.zejian.myapplication.anima.attention.BaseViewAnimator;
import com.zejian.myapplication.anima.attention.BounceAnimator;
import com.zejian.myapplication.anima.attention.FlashAnimator;
import com.zejian.myapplication.anima.attention.PulseAnimator;
import com.zejian.myapplication.anima.attention.RollInAnimator;
import com.zejian.myapplication.anima.attention.RollOutAnimator;
import com.zejian.myapplication.anima.attention.RubberBandAnimator;
import com.zejian.myapplication.anima.attention.ShakeAnimator;
import com.zejian.myapplication.anima.attention.StandUpAnimator;
import com.zejian.myapplication.anima.attention.SwingAnimator;
import com.zejian.myapplication.anima.attention.TadaAnimator;
import com.zejian.myapplication.anima.attention.WaveAnimator;
import com.zejian.myapplication.anima.attention.WobbleAnimator;
import com.zejian.myapplication.anima.bouncing_entrances.BounceInAnimator;
import com.zejian.myapplication.anima.bouncing_entrances.BounceInDownAnimator;
import com.zejian.myapplication.anima.bouncing_entrances.BounceInLeftAnimator;
import com.zejian.myapplication.anima.bouncing_entrances.BounceInRightAnimator;
import com.zejian.myapplication.anima.bouncing_entrances.BounceInUpAnimator;
import com.zejian.myapplication.anima.zooming_entrances.ZoomInAnimator;
import com.zejian.myapplication.anima.zooming_entrances.ZoomInDownAnimator;
import com.zejian.myapplication.anima.zooming_entrances.ZoomInLeftAnimator;
import com.zejian.myapplication.anima.zooming_entrances.ZoomInRightAnimator;
import com.zejian.myapplication.anima.zooming_entrances.ZoomInUpAnimator;
import com.zejian.myapplication.anima.zooming_exits.ZoomOutAnimator;
import com.zejian.myapplication.anima.zooming_exits.ZoomOutDownAnimator;
import com.zejian.myapplication.anima.zooming_exits.ZoomOutLeftAnimator;
import com.zejian.myapplication.anima.zooming_exits.ZoomOutRightAnimator;
import com.zejian.myapplication.anima.zooming_exits.ZoomOutUpAnimator;

public enum Techniques {

    Flash(FlashAnimator.class),
    Pulse(PulseAnimator.class),
    RubberBand(RubberBandAnimator.class),
    Shake(ShakeAnimator.class),
    Swing(SwingAnimator.class),
    Wobble(WobbleAnimator.class),
    Bounce(BounceAnimator.class),
    Tada(TadaAnimator.class),
    StandUp(StandUpAnimator.class),
    Wave(WaveAnimator.class),

    RollIn(RollInAnimator.class),
    RollOut(RollOutAnimator.class),

    BounceIn(BounceInAnimator.class),
    BounceInDown(BounceInDownAnimator.class),
    BounceInLeft(BounceInLeftAnimator.class),
    BounceInRight(BounceInRightAnimator.class),
    BounceInUp(BounceInUpAnimator.class),

    ZoomIn(ZoomInAnimator.class),
    ZoomInDown(ZoomInDownAnimator.class),
    ZoomInLeft(ZoomInLeftAnimator.class),
    ZoomInRight(ZoomInRightAnimator.class),
    ZoomInUp(ZoomInUpAnimator.class),

    ZoomOut(ZoomOutAnimator.class),
    ZoomOutDown(ZoomOutDownAnimator.class),
    ZoomOutLeft(ZoomOutLeftAnimator.class),
    ZoomOutRight(ZoomOutRightAnimator.class),
    ZoomOutUp(ZoomOutUpAnimator.class);



    private Class animatorClazz;

    private Techniques(Class clazz) {
        animatorClazz = clazz;
    }

    public BaseViewAnimator getAnimator() {
        try {
            return (BaseViewAnimator) animatorClazz.newInstance();
        } catch (Exception e) {
            throw new Error("Can not init animatorClazz instance");
        }
    }
}
