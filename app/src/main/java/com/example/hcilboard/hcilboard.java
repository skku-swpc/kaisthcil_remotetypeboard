package com.example.hcilboard;

import java.util.Queue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;


public class hcilboard extends InputMethodService {
	private hcilboardview mhcilboardview;
	int val = 0;
	Handler handler;
	String fingerInfo = "";
	public final int HOVER_DOWN=1, HOVER_MOVE=2, HOVER_UP=3, TOUCH_DOWN=4, TOUCH_MOVE=5, TOUCH_UP=6 ;
    TouchInfoReceiver receiver = new TouchInfoReceiver();
    public class TouchInfoReceiver extends BroadcastReceiver{
    	
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String name = intent.getAction();
			//testString =  intent.getExtras().getString("param1");
			if(name.equals("smarttv.fingerinformation")){
				fingerInfo = intent.getExtras().getString("param1");

				String[] words = fingerInfo.split(",");	

				if(words.length < 5) {
					
					return;}
				
				int state = Integer.parseInt(words[0]);
				long bId = Long.parseLong(words[1]);
				int x = Integer.parseInt(words[2]);
				int y = Integer.parseInt(words[3]);
				int z = Integer.parseInt(words[4]);
				
				int i=0;
			    switch (state) {
		        case HOVER_DOWN:
		        	if(mhcilboardview!=null&&mhcilboardview.kv!=null)
		        		mhcilboardview.kv.addHover(bId, x,y-(720-350));//350 <-> 410
//		        	b_list.add(new BlobInfo(bId,x,y,z));
//		        	hover_flag=true;
		        	break;
		        case HOVER_MOVE:
		        	if(mhcilboardview!=null&&mhcilboardview.kv!=null)
		        		mhcilboardview.kv.changeHover(bId, x,y-(720-350));//350 <-> 410
		        	//Log.i("HBOARD", bId+", "+x+"," +y);
//		        	hover_flag=true;
//		        	if(b_list.size()!=0){
//		        		BlobInfo temp=b_list.get(0);
//		        		while(temp!=null){
//		        			if(bId==temp.bid){
//		        				temp.setXYZ(x, y, z);
//		        				break;
//		        			}
//		        			i++;
//		        			temp=b_list.get(i);
//		        		}		        	
//		        	}
		        	break;
		        case HOVER_UP:
		        	if(mhcilboardview!=null&&mhcilboardview.kv!=null)
		        		mhcilboardview.kv.deleteHover(bId);
//		        	dragFlag = false;
//		        	touchNow = false;
//		        	
//		        	}
//		        	
		        	break;
	
		        }
//			    myCanvas.postInvalidate();
	        }
		}
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		IntentFilter filter = new IntentFilter();
//        filter.addAction("smarttv.fingerinformation");
//		registerReceiver(receiver, filter, null, null);
        updateFullscreenMode();
		//ththread thr=new ththread();
		//thr.start();
	}
	/*class ththread extends Thread{
		
		boolean isshown=false;
		public void run(){
			try {
				while(true){
				Thread.sleep(1000);
				isshown=isInputViewShown();
				Log.i("Isshown",Boolean.toString(isshown));
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
	public View onCreateInputView() {
		final hcilboardview hboardview = (hcilboardview) getLayoutInflater()
				.inflate(R.layout.hcilboard, null);

		hboardview
				.setOnCharacterEnteredListener(new OnCharacterEnteredListener() {
					@Override
					public void characterEntered(String character) {
						if(character.equals("|")){
							getCurrentInputConnection().deleteSurroundingText(1, 0);
						}
						else
						getCurrentInputConnection().commitText(character, 1);
						

					}
				});
		/*
		 * hboardview.setOnBackspacePressedListener(new
		 * OnBackspacePressedListener() {
		 * 
		 * @Override public void backspacePressed(boolean isLongClick) { if
		 * (isLongClick) { deleteLastWord(); } else {
		 * getCurrentInputConnection().deleteSurroundingText(1, 0); } } });
		 */

		mhcilboardview = hboardview;
		IntentFilter filter = new IntentFilter();
        filter.addAction("smarttv.fingerinformation");
		registerReceiver(receiver, filter, null, null);
		return hboardview;
	}
	
	@Override
	public void onFinishInputView(boolean finishingInput) {
		// TODO Auto-generated method stub
		mhcilboardview.kv.h_list.clear();
		super.onFinishInputView(finishingInput);

	}
	@Override
	public boolean onEvaluateFullscreenMode() {
		// TODO Auto-generated method stub
		
		return false;
	}
	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		if (mhcilboardview != null) {
			final Queue<Character> symbolsQueue = mhcilboardview
					.getSymbolsQueue();
			while (!symbolsQueue.isEmpty()) {
				final Character character = symbolsQueue.poll();
				getCurrentInputConnection().commitText(
						String.valueOf(character), 1);
			}
		}
	}

	/**
	 * Deletes one word before the cursor.
	 */
	/*
	 * private void deleteLastWord() { final int charactersToGet = 20; final
	 * String splitRegexp = " ";
	 * 
	 * // delete trailing spaces while
	 * (getCurrentInputConnection().getTextBeforeCursor(1,
	 * 0).toString().equals(splitRegexp)) {
	 * getCurrentInputConnection().deleteSurroundingText(1, 0); }
	 * 
	 * // delete last word letters final String[] words =
	 * getCurrentInputConnection().getTextBeforeCursor(charactersToGet,
	 * 0).toString() .split(splitRegexp);
	 * getCurrentInputConnection().deleteSurroundingText(words[words.length -
	 * 1].length(), 0); }
	 */
}
