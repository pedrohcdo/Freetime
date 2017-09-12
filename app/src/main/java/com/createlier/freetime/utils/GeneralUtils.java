package com.createlier.freetime.utils;


import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.createlier.freetime.entity.SectorsManager;
import com.createlier.freetime.exceptions.DataUtilsException;
import com.createlier.freetime.exceptions.SecurityUtilsException;
import com.createlier.freetime.exceptions.SyncThreadPassException;
import com.createlier.freetime.exceptions.SyncThreadPassTimeoutException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * General Utils
 * 
 * @author Pedro Henrique
 *
 */
final public class GeneralUtils {

	/** Private Constructor */
	private GeneralUtils() {
	}

	/**
	 * Location Fix Utils
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class LocationFixUtils {

	}

	/**
	 * Data Utils
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class DataUtils {

		// Final Private Static Variables
		private static char[] sHexTable = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
				'F' };
		private static HashMap<String, Integer> sCountryCodes = new HashMap<String, Integer>();

		// Initialize Country Codes
		static {
			final String keys[] = { "AF", "AL", "DZ", "AD", "AO", "AR", "AM", "AW", "AU", "AT", "AZ", "BH", "BD", "BY",
					"BE", "BZ", "BJ", "BT", "BO", "BA", "BW", "BR", "BN", "BG", "BF", "MM", "BI", "KH", "CM", "CA",
					"CV", "CF", "TD", "CL", "CN", "CX", "CC", "CO", "KM", "CG", "CD", "CK", "CR", "HR", "CU", "CY",
					"CZ", "DK", "DJ", "TL", "EC", "EG", "SV", "GQ", "ER", "EE", "ET", "FK", "FO", "FJ", "FI", "FR",
					"PF", "GA", "GM", "GE", "DE", "GH", "GI", "GR", "GL", "GT", "GN", "GW", "GY", "HT", "HN", "HK",
					"HU", "IN", "ID", "IR", "IQ", "IE", "IM", "IL", "IT", "CI", "JP", "JO", "KZ", "KE", "KI", "KW",
					"KG", "LA", "LV", "LB", "LS", "LR", "LY", "LI", "LT", "LU", "MO", "MK", "MG", "MW", "MY", "MV",
					"ML", "MT", "MH", "MR", "MU", "YT", "MX", "FM", "MD", "MC", "MN", "ME", "MA", "MZ", "NA", "NR",
					"NP", "NL", "AN", "NC", "NZ", "NI", "NE", "NG", "NU", "KP", "NO", "OM", "PK", "PW", "PA", "PG",
					"PY", "PE", "PH", "PN", "PL", "PT", "PR", "QA", "RO", "RU", "RW", "BL", "WS", "SM", "ST", "SA",
					"SN", "RS", "SC", "SL", "SG", "SK", "SI", "SB", "SO", "ZA", "KR", "ES", "LK", "SH", "PM", "SD",
					"SR", "SZ", "SE", "CH", "SY", "TW", "TJ", "TZ", "TH", "TG", "TK", "TO", "TN", "TR", "TM", "TV",
					"AE", "UG", "GB", "UA", "UY", "US", "UZ", "VU", "VA", "VE", "VN", "WF", "YE", "ZM", "ZW" };
			final int values[] = { 93, 355, 213, 376, 244, 54, 374, 297, 61, 43, 994, 973, 880, 375, 32, 501, 229, 975,
					591, 387, 267, 55, 673, 359, 226, 95, 257, 855, 237, 1, 238, 236, 235, 56, 86, 61, 61, 57, 269, 242,
					243, 682, 506, 385, 53, 357, 420, 45, 253, 670, 593, 20, 503, 240, 291, 372, 251, 500, 298, 679,
					358, 33, 689, 241, 220, 995, 49, 233, 350, 30, 299, 502, 224, 245, 592, 509, 504, 852, 36, 91, 62,
					98, 964, 353, 44, 972, 39, 225, 81, 962, 7, 254, 686, 965, 996, 856, 371, 961, 266, 231, 218, 423,
					370, 352, 853, 389, 261, 265, 60, 960, 223, 356, 692, 222, 230, 262, 52, 691, 373, 377, 976, 382,
					212, 258, 264, 674, 977, 31, 599, 687, 64, 505, 227, 234, 683, 850, 47, 968, 92, 680, 507, 675, 595,
					51, 63, 870, 48, 351, 1, 974, 40, 7, 250, 590, 685, 378, 239, 966, 221, 381, 248, 232, 65, 421, 386,
					677, 252, 27, 82, 34, 94, 290, 508, 249, 597, 268, 46, 41, 963, 886, 992, 255, 66, 228, 690, 676,
					216, 90, 993, 688, 971, 256, 44, 380, 598, 1, 998, 678, 39, 58, 84, 681, 967, 260, 263 };
			if (keys.length != values.length)
				throw new DataUtilsException("The Country keys size has to be the same values size.");
			for (int i = 0; i < keys.length; i++) {
				sCountryCodes.put(keys[i], values[i]);
			}
		}

		/**
		 * Byte To Hex String
		 * 
		 * @param array
		 * @return
		 */
		public static String byteToHexString(final byte[] array) {
			String string = "";
			for (int i = 0; i < array.length; ++i) {
				// Remove sign
				int di = ((int) array[i] + 256) & 0xFF;
				string += sHexTable[(di >> 4) & 0xF];
				string += sHexTable[di & 0xF];
			}
			return string;
		}

		/**
		 * Get Country Code
		 * 
		 * @param countryPreffix
		 * @return
		 */
		public static int getCountryCode(final String countryPreffix) {
			return sCountryCodes.get(countryPreffix.toUpperCase(Locale.US));
		}

		/**
		 * Get Country Preffix
		 * 
		 * @return
		 */
		public static List<String> createCountryPreffixList(final String appendLeft, final String appendRight) {
			final List<String> list = new ArrayList<String>();
			for (final String preffix : sCountryCodes.keySet())
				list.add(appendLeft + preffix + appendRight);
			return list;
		}

		/**
		 * Get Country Preffix
		 * 
		 * @return
		 */
		public static List<String> createCountryPreffixList() {
			final List<String> list = new ArrayList<String>();
			for (final String preffix : sCountryCodes.keySet())
				list.add(preffix);
			return list;
		}
	}

	/**
	 * Math Utils
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class MathUtils {

	}

	/**
	 * Map Utils
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class MapUtils {

	}

	/**
	 * Security Utils
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class SecurityUtils {

		/**
		 * Digest Message
		 *
		 * @param algorithm
		 * @return
		 */
		private static String digestSample(final String sample, final String algorithm) {
			MessageDigest message = null;
			try {
				// Get Algorithm
				message = MessageDigest.getInstance(algorithm);
				// Update Encode
				try {
					message.update(sample.getBytes("UTF-8"));
				} catch (final UnsupportedEncodingException e) {
					e.printStackTrace();
					message.update(sample.getBytes());
				}
				//
				return DataUtils.byteToHexString(message.digest());
			} catch (final NoSuchAlgorithmException e) {
				throw new SecurityUtilsException("Error on get MD5 Instance. Algorithm not found.");
			}
		}

		/**
		 * Encrypt sample with MD5 algorithm
		 * 
		 * @param sample
		 * 
		 * @return Hash
		 */
		public static String md5(final String sample) {
			return digestSample(sample, "MD5");
		}
	}

	/**
	 * Safety Lock
	 * <p>
	 * Used to block a process safely.
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class SafetyLockedLooper {

		/**
		 * Interception used to intercept a launched lock.
		 * <p>
		 * 
		 * @author Pedro Henrique
		 *
		 */
		public interface Interception {

			/** On Intercept callback */
			public boolean onIntercept();
		}

		/**
		 * Locks a process at a given time in seconds. To break the lock, return
		 * true on Interception callback.
		 * <p>
		 * 
		 * @param delayInMillis
		 *            Time in Seconds.
		 * @return Return true if interrupt
		 */
		final public static boolean loop(final long delayInMillis, final Interception interception) {
			final long startTime = System.currentTimeMillis();
			while ((System.currentTimeMillis() - startTime) < delayInMillis) {
				// If intercepted or Interrupted
				if (interception.onIntercept() || Thread.currentThread().isInterrupted())
					return true;
			}
			// Not interrupt
			return false;
		}

		/**
		 * Locks a process. To break the lock, return true on Interception
		 * callback.
		 * <p>
		 *
		 * @return true to intercept
		 */
		final public static boolean loop(final Interception interception) {
			while (true) {
				//
				if (interception.onIntercept())
					return false;
				// If intercepted or Interrupted
				if (Thread.currentThread().isInterrupted())
					return true;
			}
		}
	}

	/**
	 * Sync Thread Pass
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class SyncThreadPass {

		/**
		 * Identifier
		 * 
		 * @author user
		 *
		 */
		final private static class Identifier {
			volatile public Class<?> syncClass;
			volatile public AtomicBoolean syncKeyLock = new AtomicBoolean(false);
			volatile public Thread syncThread;
		}

		// Final Private Variables
		final private static List<Identifier> mIdentifiers = new ArrayList<Identifier>();

		/**
		 * Update Identifier
		 *
		 */
		private static Identifier updateIdentifier(final Class<?> syncClass) {
			synchronized (mIdentifiers) {
				for (final Identifier search : mIdentifiers) {
					if (search.syncClass.equals(syncClass))
						return search;
				}
				final Identifier newIdentifier = new Identifier();
				newIdentifier.syncClass = syncClass;
				mIdentifiers.add(newIdentifier);
				return newIdentifier;
			}
		}

		/**
		 * Return true if current Thread have acess
		 * 
		 * @param syncClass
		 * @return
		 */
		public static void assertCurrentThreadPass(final Class<?> syncClass) {
			final Identifier identifier = updateIdentifier(syncClass);
			if (identifier.syncKeyLock.get() == true && identifier.syncThread == Thread.currentThread())
				return;
			throw new SyncThreadPassException(
					"The class '" + syncClass.getName() + "' require SyncThread pass to access public methods.");
		}

		/**
		 * Obtain Sync Key<br>
		 * Thread-Safe Passport
		 * 
		 * @return
		 */
		public static void obtainSyncThreadPass(final Class<?> syncClass, final int sleepIntervals, final int maxSleep) throws InterruptedException, SyncThreadPassTimeoutException {
			final Identifier identifier = updateIdentifier(syncClass);
			if (identifier.syncKeyLock.get() == true && identifier.syncThread == Thread.currentThread())
				throw new SyncThreadPassException(
						"The class '" + syncClass.getName() + "' already has a SyncThread passage.");
			final long ts = System.currentTimeMillis();
			while (identifier.syncKeyLock.getAndSet(true)) {
				Thread.sleep(sleepIntervals);
				if(maxSleep >= 0 && (System.currentTimeMillis() - ts) >= maxSleep)
					throw new SyncThreadPassTimeoutException();
			}
			identifier.syncThread = Thread.currentThread();
		}

		/**
		 * Release Sync Key
		 * 
		 */
		public static void releaseSyncThreadPass(final Class<?> syncClass) {
			final Identifier identifier = updateIdentifier(syncClass);
			if (identifier.syncKeyLock.get() == true && identifier.syncThread == Thread.currentThread()) {
				identifier.syncThread = null;
				identifier.syncKeyLock.set(false);
			}
		}
	}

	/**
	 * Gui Tool Kit
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class GUIToolKit {

		/**
		 * 
		 * @author Pedro
		 *
		 */
		final public static class MaskEditTextChangedListener implements TextWatcher {

			// Private Variables
		    private String mMask;
		    private String mInputMask;
		    private EditText mEditText;
		    private Set<String> symbolMask = new HashSet<String>();
		    private boolean isUpdating;
		    private String old = "";

		    /**
		     * Constructor
		     * 
		     * @param mask
		     * @param editText
		     */
		    public MaskEditTextChangedListener(String mask, String inputMask, EditText editText) {
		        mMask = mask;
		        mInputMask = inputMask;
		        mEditText = editText;
		        initSymbolMask();
		        updateInputMask();
		    }
		    
		    /**
		     * Init Symbol Mask
		     */
		    private void initSymbolMask(){
		        for (int i=0; i < mMask.length(); i++){
		            char ch = mMask.charAt(i);
		            if (ch != '#')
		                symbolMask.add(String.valueOf(ch));
		        }
		    }
		    
		    /**
		     * Text Changed
		     */
		    @Override
		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		        String str = StringMask.unmask(s.toString(), symbolMask);
		        String mascara = "";

		        if (isUpdating) {
		            old = str;
		            isUpdating = false;
		            return;
		        }

		        if(str.length() > old.length())
		            mascara = StringMask.mask(mMask,str);
		        else
		            mascara = s.toString();

		        isUpdating = true;

		        mEditText.setText(mascara);
		        mEditText.setSelection(mascara.length());
		        
		        updateInputMask();
		        
		    }

		    /**
		     * Update Input Mask
		     */
		    private void updateInputMask() {
		    	int cursor = Math.min(mInputMask.length()-1, mEditText.getSelectionStart());
		    	char inputType = mInputMask.charAt(cursor);
		    	if(inputType == 'd') {
		    		mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		    	} else if(inputType == 'c') {
		    		mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
		    	} else if(inputType == 'n') {
		    		mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME| InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		    	} else if(inputType == 'e') {
		    		mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME| InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		    	} else if(inputType == 'l') {
		    		mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
		    	}
		    }
		    
		    /**
		     * Resolve Input
		     */
		    public void resolveInput() {
		    	int cursor = Math.min(mInputMask.length()-1, mEditText.getSelectionStart());
		    	char inputType = mInputMask.charAt(cursor);
		    	if(inputType == 'd') {
		    		mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		    	} else if(inputType == 'c') {
		    		mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
		    	} else if(inputType == 'n') {
		    		mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME| InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		    	} else if(inputType == 'e') {
		    		mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME| InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		    	} else if(inputType == 'l') {
		    		mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
		    	}
		    }
		    
		    /** Unused */
		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}


		    /** Unused */
		    @Override
		    public void afterTextChanged(Editable s) {}
		}
		
		/**
		 * Function
		 * 
		 * @author Pedro Henrique
		 *
		 */
		public static interface DistributeFunction {

			/**
			 * Distribute
			 * 
			 * @return Return true if distributed on this item
			 */
			public void distribute(final int id);
		}

		/**
		 * Distribute Function
		 */
		public static void distributeActions(final LinearLayout layout, final DistributeFunction function) {
			int count = 0;
			for (int i = 0; i < layout.getChildCount(); i++) {
				final View v = layout.getChildAt(i);
				final int j = count++;
				v.setOnClickListener(new View.OnClickListener() {

					/**
					 * Click
					 */
					@Override
					public void onClick(View v) {
						function.distribute(j);
					}
				});
			}
		}

		/**
		 * Distribute Actions
		 * 
		 * @param layout
		 * @param sectorsManager
		 */
		public static void distributeActions(final LinearLayout layout, final SectorsManager sectorsManager) {
			distributeActions(layout, new DistributeFunction() {

				/**
				 * Distribute
				 */
				@Override
				public void distribute(int id) {
					sectorsManager.setSector(id);
				}
			});
		}

		/**
		 * Fit List View Height
		 * 
		 * @param listView
		 */
		public static void fitListViewHeight(final ListView listView) {
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null)
				return;
			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
			listView.requestLayout();
		}

		/**
		 * Fit List View Height
		 *
		 */
		public static void fitListViewHeight(final RecyclerView recyclerView) {
			RecyclerView.Adapter<RecyclerView.ViewHolder> listAdapter = recyclerView.getAdapter();
			if (listAdapter == null)
				return;
			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getItemCount(); i++) {
				RecyclerView.ViewHolder listItem = listAdapter.createViewHolder(recyclerView, listAdapter.getItemViewType(i));
				listItem.itemView.measure(0, 0);
				totalHeight += listItem.itemView.getMeasuredHeight();
			}
			ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)recyclerView.getLayoutParams();
			params.height = totalHeight;// + (0 * (listAdapter.getCount() - 1));
			recyclerView.setLayoutParams(params);
			recyclerView.requestLayout();
		}

	}

	/**
	 * Result
	 * 
	 * @author user
	 *
	 * @param <T>
	 */
	public interface OnResultListener<T> {

		/**
		 * On Result
		 * 
		 * @param result
		 */
		public void onResult(T result);
	}

	/**
	 * String Mask
	 * 
	 * @author Pedro
	 *
	 */
	final public static class StringMask {
		
		/**
		 * Private Constructor
		 */
		private StringMask() {}
		
		/**
		 * Unmask
		 * 
		 * @param s
		 * @param replaceSymbols
		 * @return
		 */
		public static String unmask(String s, Set<String> replaceSymbols) {
			for (String symbol : replaceSymbols)
				s = s.replaceAll("[" + symbol + "]", "");
			return s;
		}
		
		/**
		 * Mask
		 * 
		 * @param format
		 * @param text
		 * @return
		 */
		public static String mask(String format, String text) {
			String maskedText = "";
			int i = 0;
			for (char m : format.toCharArray()) {
				if (m != '#') {
					maskedText += m;
					continue;
				}
				try {
					maskedText += text.charAt(i);
				} catch (Exception e) {
					break;
				}
				i++;
			}
			return maskedText;
		}
	}
}
