package com.example.hcilboard;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class keyboardview extends SurfaceView {

	SurfaceHolder mHolder;
	List<Key> key_list = new ArrayList<Key>();
	float genkey_size = 80;
	float genkey_vertical_margin = 5;
	float genkey_horizontal_margin = 5;

	public boolean gestureDraw = false;
	boolean split = true;
	float splitInterval = 300;
	public Map<Long, Point> h_list = new HashMap<Long, Point>();
	public Map<Integer, TPoint> t_list = new HashMap<Integer, TPoint>();
	public Map<Integer, KeyPlusTime> pressed_key_list = new HashMap<Integer, KeyPlusTime>();
	public List<PointF> g_list = new ArrayList<PointF>();
	
	public void init() {
		mHolder = getHolder();
		mHolder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub

			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				setWillNotDraw(false);
				Canvas c = holder.lockCanvas(null);
				draw(c);
				// postInvalidate();
				holder.unlockCanvasAndPost(c);
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				// TODO Auto-generated method stub

			}
		});

		createKeyList();

		paintChar = new Paint();
		paintAltChar = new Paint();
		paintKey = new Paint();

		paintChar.setColor(Color.WHITE);
		paintChar.setTextSize(Key.keycharTextSize);
		paintChar.getTextBounds("abcdefghijklmnopqrstuvwxyz", 0, 26, textSize);
		Key.keyTextHeight = textSize.height();
		paintChar.setAntiAlias(true);

		paintAltChar.setColor(0xff888888);
		paintAltChar.setTextSize(Key.alterKeycharTextSize);
		paintAltChar.getTextBounds("abcdefghijklmnopqrstuvwxyz", 0, 26, textSize);
		Key.alterKeyTextHeight = textSize.height();
		paintAltChar.setAntiAlias(true);
		for (int i = 0; i < key_list.size(); i++) {
			key_list.get(i).updateKeyWidth(paintChar, paintAltChar);
		}

	}

	public keyboardview(Context context) {
		this(context, null);
	}

	public keyboardview(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public keyboardview(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	void createKeyList() {
		char[] firstRow = { 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p' };
		char[] firstRowAlt = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
		int firstRowSplitIndex = 5;
		char[] secondRow = { 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l' };
		char[] secondRowAlt = { '~', '!', '@', '#', '^', '&', '(', ')', '-' };
		int secondRowSplitIndex = 5;
		char[] thirdRow = { 'z', 'x', 'c', 'v', 'b', 'n', 'm' };
		char[] thirdRowAlt = { ';', ':', '/', '\"', ',', '?', '_' };
		int thirdRowSplitIndex = 4;

		float keyboardheight = 350f;
		float keyboardwidth = 1280f;

		float keyHeight = (keyboardheight - 10 * genkey_vertical_margin) / 4;

		float defaultKeyWidth = 0;
		float keyboardPadding = 40;

		if (split) {
			defaultKeyWidth = (keyboardwidth - ((firstRow.length - 2) * 2) * genkey_horizontal_margin - splitInterval - 2 * keyboardPadding) / firstRow.length;
			Key key = null;
			for (int i = 0; i < firstRowSplitIndex; i++) { // First Row, Leftside
				key = new Key(keyboardPadding + genkey_horizontal_margin * (i * 2) + defaultKeyWidth * i, genkey_vertical_margin * 2, firstRow[i],
						firstRowAlt[i], keyHeight, defaultKeyWidth);
				Log.d(firstRow[i] + "", key.px + ", " + key.py);
				key_list.add(key);
			}

			float defaultsplitleft = keyboardPadding + 5 * defaultKeyWidth + 8 * genkey_horizontal_margin + splitInterval;

			// float firstsplitleft = key.keyRect.right + splitInterval;
			float firstsplitleft = defaultsplitleft;
			for (int i = firstRowSplitIndex; i < firstRow.length; i++) {// First Row, Rightside
				key = new Key(firstsplitleft + (i - firstRowSplitIndex) * genkey_horizontal_margin * 2 + (i - firstRowSplitIndex) * defaultKeyWidth,
						genkey_vertical_margin * 2, firstRow[i], firstRowAlt[i], keyHeight, defaultKeyWidth);
				key_list.add(key);
			}

			for (int i = 0; i < secondRowSplitIndex; i++) { // Second Row, Leftside
				key = new Key(keyboardPadding + genkey_horizontal_margin * (i * 2) + defaultKeyWidth * i, genkey_vertical_margin * 4 + keyHeight, secondRow[i],
						secondRowAlt[i], keyHeight, defaultKeyWidth);
				Log.d(secondRow[i] + "", key.px + ", " + key.py);
				key_list.add(key);
			}

			float secondsplitleft = defaultsplitleft;

			for (int i = secondRowSplitIndex; i < secondRow.length; i++) {// Second Row, Rightside
				key = new Key(secondsplitleft + (i - secondRowSplitIndex) * genkey_horizontal_margin * 2 + (i - secondRowSplitIndex) * defaultKeyWidth,
						genkey_vertical_margin * 4 + keyHeight, secondRow[i], secondRowAlt[i], keyHeight, defaultKeyWidth);
				key_list.add(key);
			}

			for (int i = 0; i < thirdRowSplitIndex; i++) { // Third Row, Leftside
				key = new Key(keyboardPadding + 1f * defaultKeyWidth + 2 * genkey_horizontal_margin + genkey_horizontal_margin * (i * 2) + defaultKeyWidth * i,
						genkey_vertical_margin * 6 + keyHeight * 2, thirdRow[i], thirdRowAlt[i], keyHeight, defaultKeyWidth);
				Log.d(secondRow[i] + "", key.px + ", " + key.py);
				key_list.add(key);
			}

			float thirdsplitleft = defaultsplitleft;

			for (int i = thirdRowSplitIndex; i < thirdRow.length; i++) {// Third Row, Rightside
				key = new Key(thirdsplitleft + (i - thirdRowSplitIndex) * genkey_horizontal_margin * 2 + (i - thirdRowSplitIndex) * defaultKeyWidth,
						genkey_vertical_margin * 6 + keyHeight * 2, thirdRow[i], thirdRowAlt[i], keyHeight, defaultKeyWidth);
				key_list.add(key);
			}

			float bsWidth = 2 * defaultKeyWidth + 2 * genkey_horizontal_margin;
			key = new Key(key.keyRect.right + 2 * genkey_horizontal_margin, key.keyRect.top, '|', '|', keyHeight, bsWidth);
			key_list.add(key);

			float togglekeyWidth = 1.5f * defaultKeyWidth + genkey_horizontal_margin;
			key = new Key(keyboardPadding, 8 * genkey_vertical_margin + 3 * keyHeight, '\t', '\t', keyHeight, togglekeyWidth);
			key_list.add(key);
			key.keyCharText = "Merge";
			key.altKeyCharText = "Merge";
			key.keyUpperCharText = "Merge";
			
			float spacebar1Width = 3.5f * defaultKeyWidth + 5 * genkey_horizontal_margin;
			key = new Key(key.keyRect.right + 2 * genkey_horizontal_margin, 8 * genkey_vertical_margin + 3 * keyHeight, ' ', ' ', keyHeight, spacebar1Width);
			key_list.add(key);

			float fourthsplitleft = defaultsplitleft;

			float spacebar2Width = 2.5f * defaultKeyWidth + 3 * genkey_horizontal_margin;
			key = new Key(fourthsplitleft, 8 * genkey_vertical_margin + 3 * keyHeight, ' ', ' ', keyHeight, spacebar2Width);
			key_list.add(key);
			
			key = new Key(key.keyRect.right + 2 * genkey_horizontal_margin, 8 * genkey_vertical_margin + 3 * keyHeight, '.', '.', keyHeight, defaultKeyWidth);
			key_list.add(key);
			
			float enterWidth = 1.5f * defaultKeyWidth + 1 * genkey_horizontal_margin;
			key = new Key(key.keyRect.right + 2 * genkey_horizontal_margin, 8 * genkey_vertical_margin + 3 * keyHeight, '\n', '\n', keyHeight, enterWidth);
			key_list.add(key);

		} else {

			defaultKeyWidth = (keyboardwidth - 2 * keyboardPadding - (firstRow.length * 2 - 2) * genkey_horizontal_margin) / firstRow.length;
			Log.d("Key Size", defaultKeyWidth + ", " + keyHeight);
			float secondRowLeftMargin = defaultKeyWidth / 2;
			float thirdRowLeftMargin = secondRowLeftMargin + defaultKeyWidth + 2 * genkey_horizontal_margin;
			Key key = null;
			for (int i = 0; i < firstRow.length; i++) { // First Row, Leftside
				key = new Key(keyboardPadding + genkey_horizontal_margin * (i * 2) + defaultKeyWidth * i, genkey_vertical_margin * 2, firstRow[i],
						firstRowAlt[i], keyHeight, defaultKeyWidth);
				Log.d(firstRow[i] + "", key.px + ", " + key.py);
				key_list.add(key);
			}

			for (int i = 0; i < secondRow.length; i++) { // Second Row, Leftside
				key = new Key(keyboardPadding + secondRowLeftMargin + genkey_horizontal_margin * (i * 2 + 1) + defaultKeyWidth * i, genkey_vertical_margin * 4
						+ keyHeight, secondRow[i], secondRowAlt[i], keyHeight, defaultKeyWidth);
				key_list.add(key);
			}

			for (int i = 0; i < thirdRow.length; i++) { // Third Row, Leftside
				key = new Key(keyboardPadding + thirdRowLeftMargin + genkey_horizontal_margin * (i * 2 + 1) + defaultKeyWidth * i, genkey_vertical_margin * 6
						+ keyHeight * 2, thirdRow[i], thirdRowAlt[i], keyHeight, defaultKeyWidth);
				key_list.add(key);
			}

			float bsWidth = keyboardwidth - keyboardPadding - key.keyRect.right - 2 * genkey_horizontal_margin;

			key = new Key(key.keyRect.right + 2 * genkey_horizontal_margin, genkey_vertical_margin * 6 + keyHeight * 2, '|', '|', keyHeight, bsWidth);
			key_list.add(key);

			float toggleWidth = 1.5f * defaultKeyWidth + genkey_horizontal_margin;
			key = new Key(keyboardPadding, 8 * genkey_vertical_margin + 3 * keyHeight, '\t', '\t', keyHeight, toggleWidth);
			key_list.add(key);

			float spacebarWidth = 6f * defaultKeyWidth + 10 * genkey_horizontal_margin;
			key = new Key(keyboardPadding + defaultKeyWidth * 1.5f + 3 * genkey_horizontal_margin, 8 * genkey_vertical_margin + 3 * keyHeight, ' ', ' ',
					keyHeight, spacebarWidth);
			key_list.add(key);
			
			key = new Key(key.keyRect.right + 2 * genkey_horizontal_margin, 8 * genkey_vertical_margin + 3 * keyHeight, '.', '.', keyHeight, defaultKeyWidth);
			key_list.add(key);
			
			float enterWidth = bsWidth;
			key = new Key(key.keyRect.right + 2 * genkey_horizontal_margin, key.py, '\n', '\n', keyHeight, enterWidth);
			key_list.add(key);
		}
	}

	public void toggleSplit() {
		if (split) {
			split = false;
		} else {
			split = true;
		}
		key_list.clear();
		createKeyList();
		for (int i = 0; i < key_list.size(); i++) {
			key_list.get(i).updateKeyWidth(paintChar, paintAltChar);
		}
	}

	protected void onDraw(Canvas canvas) {
//		canvas.drawColor(0, PorterDuff.Mode.CLEAR);
		canvas.drawColor(Color.BLACK);
		setHoverKey();
		DrawKeys(key_list, canvas);
	}

	void setHoverKey() {
		for (int ind = 0; ind < key_list.size(); ind++) {
			boolean curHover = false;
			Key tempkey = key_list.get(ind);
			if (h_list != null) {
				if (h_list.size() == 0) {

					tempkey.hovered = false;
				}

				else {
					Object[] k_set = h_list.keySet().toArray();
					for (int i = 0; i < k_set.length; i++) {
						int tempx = h_list.get((Long) k_set[i]).x;
						int tempy = h_list.get((Long) k_set[i]).y;
						if (tempx >= tempkey.px - genkey_horizontal_margin && tempx < tempkey.px + tempkey.wsize + genkey_horizontal_margin
								&& tempy >= tempkey.py - genkey_vertical_margin && tempy < tempkey.py + tempkey.hsize + genkey_vertical_margin) {
							curHover = true;

						}
					}
				}

			}
			tempkey.hovered = curHover;
		}

	}

	public void addHover(long id, int x, int y) {
		h_list.put(id, new Point(x, y));
		postInvalidate();
	}

	public void changeHover(long id, int x, int y) {

		if (h_list != null && h_list.get(id) != null) {
			h_list.remove(id);
			h_list.put(id, new Point(x, y));
		}
		if (h_list != null && !h_list.containsKey(id)) {
			h_list.put(id, new Point(x, y));
		}
		postInvalidate();
	}

	public void deleteHover(long id) {
		if (h_list != null && h_list.get(id) != null) {
			h_list.remove(id);
		}

		postInvalidate();
	}

	public void addTPoint(int id, float x, float y,long downtime) {
		t_list.put(id, new TPoint(x,y,downtime));
		postInvalidate();
	}

	public void changeTPoint(int id, float x, float y) {
		if (t_list != null && t_list.get(id) != null) {
			long downtime=t_list.get(id).touchTime;
			t_list.remove(id);
			t_list.put(id, new TPoint(x, y,downtime));
		}
		postInvalidate();
	}

	public void deleteTPoint(int id) {
		if (t_list != null && t_list.get(id) != null) {
			t_list.remove(id);
		}

		postInvalidate();
	}

	Paint paintChar;
	Paint paintAltChar;
	Paint paintKey;
	Rect textSize = new Rect();

	void DrawKeys(List<Key> k_list, Canvas c) {
		Key key;

		for (int ind = 0; ind < k_list.size(); ind++) {
			key = k_list.get(ind);

			if (key.touched == true)
				paintKey.setARGB(255, 64, 12, 12);
			else if (key.hovered == false)
				paintKey.setColor(Color.DKGRAY);
			else
				paintKey.setARGB(255, 110, 110, 0);
			c.drawRect(key.keyRect, paintKey);
			if (key.keyTextWidth == -1) {// if the width has not been set yet, measure it and update.

			}
			c.drawText(key.keyCharText, 0, key.keyCharText.length(), key.keyTextRect.left, key.keyTextRect.bottom, paintChar);
			if (key.alterKey != ' ' && key.alterKey != '\n' && key.alterKey != '|' && key.alterKey != '\t') {
				c.drawText(key.altKeyCharText, 0, key.altKeyCharText.length(), key.keyAltTextRect.left, key.keyAltTextRect.bottom, paintAltChar);
				c.drawText(key.keyUpperCharText, 0, key.keyUpperCharText.length(), key.keyUpperTextRect.left, key.keyUpperTextRect.bottom, paintAltChar);
			}
		}
	}

	public void reset() {
		for (int ind = 0; ind < key_list.size(); ind++) {
			Key tempkey = key_list.get(ind);
			if (tempkey.touched)
				tempkey.touched = false;
		}
		postInvalidate();
	}

	public void hitTest(int downMoveUp) {
		for (int ind = 0; ind < key_list.size(); ind++) {
			boolean curTouch = false;
			Key tempkey = key_list.get(ind);

			if (t_list != null) {
				if (t_list.size() == 0) {

					tempkey.touched = false;
					pressed_key_list.clear();
				}

				else {
					Object[] k_set = t_list.keySet().toArray();
					for (int i = 0; i < k_set.length; i++) {
						float tempx = t_list.get((Integer) k_set[i]).px;
						float tempy = t_list.get((Integer) k_set[i]).py;
						long tTime=t_list.get((Integer) k_set[i]).touchTime;
						if (tempx >= tempkey.px - genkey_horizontal_margin && tempx < tempkey.px + tempkey.wsize + genkey_horizontal_margin
								&& tempy >= tempkey.py - genkey_vertical_margin && tempy < tempkey.py + tempkey.hsize + genkey_vertical_margin) {
							curTouch = true;
							//Log.i("input", (Integer.toString((Integer) k_set[i])) + "," + Integer.toString(k_set.length));
							if (!pressed_key_list.containsKey((Integer) k_set[i])) {
								pressed_key_list.put((Integer) k_set[i], new KeyPlusTime(tempkey,tTime));
							}

						}
					}
				}

			}
			tempkey.touched = curTouch;
		}
	}

	public char checkIfAltered(char basekey, float px, float py) {
		char returnkey = basekey;
		boolean noAlter = false;
		double compared = 10000;
		Key tempchar = null;
		long downtime=-1;
		if (pressed_key_list.size() > 0) {
			Object[] pk_set = pressed_key_list.keySet().toArray();
			for (int i = 0; i < pk_set.length; i++) {
				if (pressed_key_list.get((Integer) pk_set[i]).key.keychar == (basekey)) {
					pressed_key_list.remove((Integer) pk_set[i]);
					noAlter = true;
					break;
				}
			}
			if (noAlter == false) {
				for (int i = 0; i < pk_set.length; i++) {
					Key tempkey = pressed_key_list.get((Integer) pk_set[i]).key;
					long temptime=pressed_key_list.get((Integer) pk_set[i]).tTime;
					if (Math.abs(tempkey.px - px) < compared) {
						compared = Math.abs(tempkey.px - px);
						tempchar = tempkey;
						downtime=temptime;
						//Log.i("HERE", "EEEE");
					}
				}
				if (tempchar != null) {
					if(System.currentTimeMillis()-downtime>500){
					if (tempchar.px - px < 0)
						returnkey = tempchar.alterKey;
					else {
						if (tempchar.keychar != '|' && tempchar.keychar != '\n' && tempchar.keychar != '\0' && tempchar.keychar != ' ')
							returnkey = Character.toUpperCase(tempchar.keychar);
						else
							returnkey = basekey;
					}
					}
					else
						returnkey=tempchar.keychar;
				}

			}
		}

		return returnkey;
	}

	public char findkey(float px, float py, int Motion) {
		char returnkey = '\0';
		for (int ind = 0; ind < key_list.size(); ind++) {
			Key tempkey = key_list.get(ind);

			if (px >= tempkey.px - genkey_horizontal_margin && px < tempkey.px + tempkey.wsize + genkey_horizontal_margin
					&& py >= tempkey.py - genkey_vertical_margin && py < tempkey.py + tempkey.hsize + genkey_vertical_margin) {
				if (Motion == MotionEvent.ACTION_DOWN) {
					returnkey = tempkey.keychar;
				}
				if (Motion == MotionEvent.ACTION_UP) {
					returnkey = tempkey.keychar;
				}
			}
		}
		postInvalidate();
		return returnkey;
	}

	public char findCurKey(float px, float py) {
		char returnkey = '\0';
		for (int ind = 0; ind < key_list.size(); ind++) {
			Key tempkey = key_list.get(ind);
			if (px >= tempkey.px - genkey_horizontal_margin && px < tempkey.px + tempkey.wsize + genkey_horizontal_margin
					&& py >= tempkey.py - genkey_vertical_margin && py < tempkey.py + tempkey.hsize + genkey_vertical_margin) {

				returnkey = tempkey.keychar;

			}

		}
		postInvalidate();
		return returnkey;
	}

}
class TPoint{
	public float px;
	public float py;
	public long touchTime;
	public TPoint(float x, float y, long Time){
		this.px=x;
		this.py=y;
		this.touchTime=Time;
	}
}
class KeyPlusTime{
	public Key key;
	public long tTime;
	public KeyPlusTime(Key key,long time){
		this.key=key;
		this.tTime=time;
	}
}
class Key {
	public float px;
	public float py;
	public char keychar;
	public char alterKey;
	public String keyCharText;
	public String keyUpperCharText;
	public String altKeyCharText;
	public boolean touched = false;
	public boolean hovered = false;
	public float hsize;
	public float wsize;
	public RectF keyRect = new RectF();
	public RectF keyTextRect = new RectF();
	public RectF keyAltTextRect = new RectF();
	public RectF keyUpperTextRect = new RectF();
	public final static float keycharTextSize = 40;
	public final static float alterKeycharTextSize = 20;
	public static float alterKeyTextHeight = -1;
	public static float keyTextHeight = -1;
	public float keyTextWidth = -1;
	public float altKeyTextWidth = -1;

	public void updateKeyWidth(Paint PaintForKey, Paint PaintForAltKey) {
		Rect bounds = new Rect();
		PaintForKey.getTextBounds(keyCharText, 0, keyCharText.length(), bounds);
		keyTextWidth = bounds.width();
		PaintForAltKey.getTextBounds(keyCharText, 0, keyCharText.length(), bounds);
		altKeyTextWidth = bounds.width();
		if (alterKey == keychar) {
			keyTextRect.left = px + wsize / 2 - keyTextWidth / 2;
			keyTextRect.right = keyTextRect.left + keyTextWidth;
			keyTextRect.top = py + hsize / 2 - keyTextHeight / 2;
			keyTextRect.bottom = keyTextRect.top + keyTextHeight;
			keyAltTextRect = keyTextRect;
		} else {
			keyTextRect.left = px + wsize / 2 - keyTextWidth / 2;
			keyTextRect.right = keyTextRect.left + keyTextWidth;
			keyTextRect.top = py + hsize / 4 - keyTextHeight / 2;
			keyTextRect.bottom = keyTextRect.top + keyTextHeight;
			keyAltTextRect.left = px + wsize * 3 / 4 - altKeyTextWidth / 2;
			keyAltTextRect.right = keyAltTextRect.left + altKeyTextWidth;
			keyAltTextRect.top = py + hsize * 3 / 4 - alterKeyTextHeight / 2;
			keyAltTextRect.bottom = keyAltTextRect.top + alterKeyTextHeight;
			keyUpperTextRect.left = px + wsize * 1 / 4 - altKeyTextWidth / 2;
			keyUpperTextRect.right = keyAltTextRect.left + altKeyTextWidth;
			keyUpperTextRect.top = py + hsize * 1 / 4 - alterKeyTextHeight / 2;
			keyUpperTextRect.bottom = keyAltTextRect.top + alterKeyTextHeight;
		}
	}

	public Key(float x, float y, char keychar, char alterkeychar, float hsize, float wsize) {
		keyRect.left = x;
		keyRect.right = x + wsize;
		keyRect.top = y;
		keyRect.bottom = y + hsize;

		px = x;
		py = y;
		this.keychar = keychar;
		this.alterKey = alterkeychar;
		switch (keychar) {
		case ' ':
			keyCharText = " ";
			altKeyCharText = " ";
			keyUpperCharText = " ";
			break;
		case '\n':
			keyCharText = "Enter";
			altKeyCharText = "Enter";
			keyUpperCharText = "Enter";
			break;
		case '|':
			keyCharText = "ก็";
			altKeyCharText = "ก็";
			keyUpperCharText = "ก็";
			break;
		case '\t':
			keyCharText = "Split";
			altKeyCharText = "Split";
			keyUpperCharText = "Split";
			break;
		default:
			keyCharText = keychar + "";
			keyUpperCharText = keyCharText.toUpperCase();
			altKeyCharText = alterkeychar + "";
			break;
		}
		this.hsize = hsize;
		this.wsize = wsize;
	}
}
