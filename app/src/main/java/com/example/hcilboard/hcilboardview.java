package com.example.hcilboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class hcilboardview extends RelativeLayout {
	private final Context mContext;
	private OnCharacterEnteredListener mOnCharacterEnteredListener;
	keyboardview kv;
	BufferedReader br;
	boolean gestureInput = false;
	String path;
	float temppx;
	float temppy;
	char downedkey;
	public Map<Integer, Point> t_list = new HashMap<Integer, Point>();
	public static ArrayList<String> WORDS = new ArrayList<String>();
	public static String[] KEYBRD_LAYOUT = { "qwertyuiop", "asdfghjkl", "zxcvbnm" };
	private final Queue<Character> mSymbolsQueue = new LinkedList<Character>();

	public hcilboardview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		this.setClickable(true);

		/*
		 * br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.wordlist2)));
		 * String idx="";
		 * 
		 * while(idx!=null){
		 * try {
		 * idx=br.readLine();
		 * } catch (IOException e) {
		 * // TODO Auto-generated catch block
		 * e.printStackTrace();
		 * }
		 * if(idx == null) break;
		 * WORDS.add(idx);
		 * }
		 */

	}

	protected void onFinishInflate() {
		kv = (keyboardview) findViewById(R.id.keyboard_view);

	}

	public void setOnCharacterEnteredListener(OnCharacterEnteredListener onCharacterEnteredListener) {
		mOnCharacterEnteredListener = onCharacterEnteredListener;
	}

	public Queue<Character> getSymbolsQueue() {
		return mSymbolsQueue;
	}

	public static ArrayList<String> checkWords(String path) {

		ArrayList<String> suggestions = new ArrayList<String>();
		ArrayList<String> tempSugg = new ArrayList<String>();
		for (int i = 0; i < WORDS.size() - 1; i++) {

			if (WORDS.get(i).charAt(0) == path.charAt(0) && WORDS.get(i).charAt(WORDS.get(i).length() - 1) == path.charAt(path.length() - 1)) {
				tempSugg.add(WORDS.get(i));
			}

		}
		for (int j = 0; j < tempSugg.size(); j++) {
			if (match(path, tempSugg.get(j))) {
				suggestions.add(tempSugg.get(j));
			}
		}

		int min_length = get_minimum_wordlength(path);
		for (int k = 0; k < suggestions.size(); k++) {
			if (suggestions.get(k).length() > min_length) {
				suggestions.remove(k);
				k -= 1;
			}

		}

		return suggestions;

	}

	public static int get_minimum_wordlength(String path) {
		String row_numbers = "";
		for (int i = 0; i < path.length(); i++) {
			row_numbers += get_keyboard_row(path.charAt(i));
		}
		row_numbers = compress(row_numbers);
		return row_numbers.length() - 3;

	}

	public static String compress(String sequence) {

		String ret = "";
		char temp;
		char prev = '\n';
		for (int i = 0; i < sequence.length(); i++) {
			temp = sequence.charAt(i);
			if (prev != temp) {
				ret += temp;
				prev = sequence.charAt(sequence.length() - 1);
			}
		}
		Log.i("UP", ret);
		return ret;

	}

	public static int get_keyboard_row(char ch) {

		for (int i = 0; i < KEYBRD_LAYOUT.length; i++)
			if (KEYBRD_LAYOUT[i].indexOf(ch) != -1)
				return i;
		return -1;

	}

	public static boolean match(String path, String word) {
		char ch;
		int pos;
		for (int i = 0; i < word.length(); i++) {
			ch = word.charAt(i);
			pos = path.indexOf(ch);
			if (pos == -1)
				return false;
			else
				path = path.substring(pos);
		}
		return true;

	}

	public boolean onTouchEvent(MotionEvent e) {
		// TODO Auto-generated method stub

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			kv.addTPoint(e.getPointerId(e.getActionIndex()), e.getX(), e.getY(),System.currentTimeMillis());
			kv.hitTest(0);
			// e.ge
			temppx = e.getX();
			temppy = e.getY();
			char tempdc = kv.findkey(e.getX(), e.getY(), MotionEvent.ACTION_DOWN);
			// t_list.put(e., value)
			// downedkey=tempdc;
			// Log.i("UP", e.getX()+", "+(e.getY()-270)+", "+tempdc);
			// if(tempdc!='\0')
			// path=Character.toString(tempdc);

			break;
		case MotionEvent.ACTION_MOVE:
			kv.changeTPoint(e.getPointerId(e.getActionIndex()), e.getX(), e.getY());
			kv.hitTest(1);
			// if(Math.sqrt(Math.pow((e.getX()-temppx), 2)+Math.pow((e.getY()-temppy),2))>30){
			// gestureInput=true;
			// kv.gestureDraw=true;
			// kv.g_list.add(new PointF(e.getX(),e.getY()));
			// Log.i("Move", "COME");
			// char tempc=kv.findCurKey(e.getX(),e.getY());
			/*
			 * if(tempc!='\0')
			 * {
			 * if(path==null)
			 * path=Character.toString(tempc);
			 * else
			 * path=path+tempc;
			 * }
			 */
			// temppx=e.getX();
			// temppy=e.getY();

			// }

			break;
		case MotionEvent.ACTION_UP:
			if (gestureInput == true) {
				// Log.i("UP", path);
				// int curlength=textview2.getText().length();
				String ans = "";
				// ArrayList<String> anslist= checkWords(path);
				/*
				 * for(int i=0;i<anslist.size();i++){
				 * if(ans.length()<anslist.get(i).length())
				 * ans=anslist.get(i);
				 * }
				 */

				// textview2.append(ans);
				// textview2.setSelection(curlength+ans.length());
				// mOnCharacterEnteredListener.characterEntered(ans);
				// path=null;

				// char tempc=kv.findCurKey(e.getX(),e.getY());
				// if(tempc!='/'||tempc!='\n'||tempc!=' '){
				// mOnCharacterEnteredListener.characterEntered(Character.toString(Character.toUpperCase(tempc)));
				// }
			}

			else {
				Log.i("UP", e.getX() + ", " + e.getY());
				char tinput = kv.findkey(e.getX(), e.getY(), MotionEvent.ACTION_UP);
				kv.deleteTPoint(e.getPointerId(e.getActionIndex()));
				char input = kv.checkIfAltered(tinput, e.getX(), e.getY());
				kv.hitTest(-1);
				switch (input) {
				case '|':
					mOnCharacterEnteredListener.characterEntered(Character.toString(input));
					break;
				case '\n':
					mOnCharacterEnteredListener.characterEntered(Character.toString(input));
					break;
				case '\0':
					break;
				case  '\t':
					kv.toggleSplit();
					break;
				default:
					mOnCharacterEnteredListener.characterEntered(Character.toString(input));
					break;
				}

			}
			gestureInput = false;
			kv.gestureDraw = false;
			kv.g_list.clear();
			if (e.getPointerCount() == 1)
				kv.reset();

			// Log.i("typeLog",Character.toString(input));
			break;
		// default:

		// break;
		}

		kv.postInvalidate();

		return super.onTouchEvent(e);
	}
}
