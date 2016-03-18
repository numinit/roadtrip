package com.numinit.interactive;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * CommandLine: A simple quick-and-dirty command-line parser
 * @author Morgan Jones
 *
 */
public class CommandLine {
	/**
	 * OptionBase: The base for the Option class
	 * @author Morgan Jones
	 *
	 */
	public static class OptionBase implements Comparable<OptionBase> {
		/**
		 * The valueOf method
		 */
		private Method _valueOf;

		/**
		 * All of this option's fields
		 */
		private String _longForm, _shortForm, _defaultValue, _description, _helpText, _value;

		/**
		 * Whether this Option is a flag
		 */
		private boolean _flag;

		/**
		 * Initializes a new Option.
		 * @param flag Whether this option is a flag
		 * @param longForm The long form (e.g. --option)
		 * @param shortForm The short form (e.g. -o)
		 * @param defaultValue Default value
		 * @param description The description
		 * @param helpText The help text
		 */
		protected OptionBase(boolean flag, String longForm, String shortForm, String defaultValue, String description, String helpText) {
			// Assign all params
			this._flag = flag;
			this._longForm = longForm.replace('-', '_');
			this._shortForm = shortForm;
			this._defaultValue = defaultValue;
			this._description = description;
			this._helpText = helpText;
			this._value = null;
		}

		@Override
		public String toString() {
			return String.format("--%s, -%s%s: %s", this.getLong(), this.getShort(), this.getHelpText() != null ? String.format(" <%s%s>", this.getHelpText(), this.getDefault() != null ? String.format("|%s", this.getDefault()) : "") : (this.getDefault() != null ? String.format("=%s", this.getDefault()) : ""), this.getDescription());
		}

		@Override
		public int compareTo(OptionBase other) {
			return this.getLong().compareTo(other.getLong());
		}

		/**
		 * Gets the long form of the Option.
		 * @return The long form
		 */
		public String getLong() {
			return this._longForm;
		}

		/**
		 * Gets the short form of this Option.
		 * @return The short form
		 */
		public String getShort() {
			return this._shortForm;
		}

		/**
		 * Gets the default value of this Option.
		 * @return The default value
		 */
		public String getDefault() {
			return this._defaultValue;
		}

		/**
		 * Returns the description of this Option.
		 * @return The description
		 */
		public String getDescription() {
			return this._description;
		}

		/**
		 * Returns the help text of this Option.
		 * @return The help text
		 */
		public String getHelpText() {
			return this._helpText;
		}

		/**
		 * Returns the string value of this Option.
		 * @return The string value
		 */
		public String getStringValue() {
			return this._value;
		}

		/**
		 * Returns whether this Option is a flag.
		 * @return Whether this Option is a flag
		 */
		public boolean isFlag() {
			return this._flag;
		}

		/**
		 * Sets the value of this Option to `value'
		 * @param value The new value
		 */
		public void setValue(String value) {
			this._value = value;
		}

		/**
		 * Sets the valueOf callback to `method'
		 * @param method The method
		 */
		protected void setMethod(Method method) {
			this._valueOf = method;
		}

		/**
		 * Gets the raw value of this type (as an Object), or null if no suitable value could be found
		 * @throws InvocationTargetException 
		 * @throws IllegalAccessException 
		 * @throws IllegalArgumentException 
		 */
		protected Object getRawValue() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			if (this.getStringValue() != null) {
				return this._valueOf.invoke(null, this.getStringValue());
			} else if (this.getDefault() != null) {
				return this._valueOf.invoke(null, this.getDefault());
			} else {
				return null;
			}
		}
	}

	/**
	 * Concrete implementation of an Option using reflection
	 * @author Morgan Jones
	 *
	 * @param <T> The type
	 */
	public static class Option<T> extends OptionBase {
		/**
		 * The class of type parameter T
		 */
		private Class<T> klass;

		/**
		 * Initializes a new Option.
		 * @param klass The class
		 * @param flag Whether this option is a flag
		 * @param longForm The long form (e.g. --option)
		 * @param shortForm The short form (e.g. -o)
		 * @param defaultValue Default value
		 * @param description The description
		 * @param helpText The help text
		 */
		public Option(Class<T> klass, boolean flag, String longForm, String shortForm, T defaultValue, String description, String helpText) {
			super(flag, longForm, shortForm, defaultValue == null ? null : defaultValue.toString(), description, helpText);
			this.klass = klass;
			try {
				this.setMethod(this.klass.getMethod("valueOf", String.class));
			} catch (Exception e) {
				try {
					this.setMethod(this.klass.getMethod("valueOf", Object.class));
				} catch (Exception f) {
					throw new RuntimeException(f);
				}
			}
		}

		/**
		 * Initializes a new Option.
		 * @param klass The class
		 * @param flag Whether this option is a flag
		 * @param longForm The long form (e.g. --option)
		 * @param shortForm The short form (e.g. -o)
		 * @param defaultValue Default value
		 * @param description The description
		 */
		public Option(Class<T> klass, boolean flag, String longForm, String shortForm, T defaultValue, String description) {
			this(klass, flag, longForm, shortForm, defaultValue, description, null);
		}

		/**
		 * Initializes a new Option.
		 * @param klass The class
		 * @param flag Whether this option is a flag
		 * @param longForm The long form (e.g. --option)
		 * @param shortForm The short form (e.g. -o)
		 * @param defaultValue Default value
		 */
		public Option(Class<T> klass, boolean flag, String longForm, String shortForm, T defaultValue) {
			this(klass, flag, longForm, shortForm, defaultValue, null, null);
		}

		/**
		 * Initializes a new Option.
		 * @param klass The class
		 * @param flag Whether this option is a flag
		 * @param longForm The long form (e.g. --option)
		 * @param shortForm The short form (e.g. -o)
		 */
		public Option(Class<T> klass, boolean flag, String longForm, String shortForm) {
			this(klass, flag, longForm, shortForm, null, null, null);
		}

		/**
		 * Initializes a new Option. Uses the first character of the long form for the short form.
		 * @param klass The class
		 * @param flag Whether this option is a flag
		 * @param longForm The long form (e.g. --option)
		 */
		public Option(Class<T> klass, boolean flag, String longForm) {
			this(klass, flag, longForm, String.format("%c", longForm.charAt(0)), null, null, null);
		}

		/**
		 * Initializes a new Option. This option will not be a flag.
		 * @param klass The class
		 * @param longForm The long form (e.g. --option)
		 * @param shortForm The short form (e.g. -o)
		 * @param defaultValue Default value
		 * @param description The description
		 * @param helpText The help text
		 */
		public Option(Class<T> klass, String longForm, String shortForm, T defaultValue, String description, String helpText) {
			this(klass, false, longForm, shortForm, defaultValue, description, helpText);
		}

		/**
		 * Initializes a new Option. This option will not be a flag.
		 * @param klass The class
		 * @param longForm The long form (e.g. --option)
		 * @param shortForm The short form (e.g. -o)
		 * @param defaultValue Default value
		 * @param description The description
		 */
		public Option(Class<T> klass, String longForm, String shortForm, T defaultValue, String description) {
			this(klass, false, longForm, shortForm, defaultValue, description, null);
		}

		/**
		 * Initializes a new Option. This option will not be a flag.
		 * @param klass The class
		 * @param longForm The long form (e.g. --option)
		 * @param shortForm The short form (e.g. -o)
		 * @param defaultValue Default value
		 */
		public Option(Class<T> klass, String longForm, String shortForm, T defaultValue) {
			this(klass, false, longForm, shortForm, defaultValue, null, null);
		}

		/**
		 * Initializes a new Option. This option will not be a flag.
		 * @param klass The class
		 * @param longForm The long form (e.g. --option)
		 * @param shortForm The short form (e.g. -o)
		 */
		public Option(Class<T> klass, String longForm, String shortForm) {
			this(klass, false, longForm, shortForm, null, null, null);
		}

		/**
		 * Initializes a new Option. This option will not be a flag. The short form will be formed from the first character of the long form.
		 * @param klass The class
		 * @param longForm The long form (e.g. --option)
		 */
		public Option(Class<T> klass, String longForm) {
			this(klass, false, longForm);
		}

		/**
		 * Returns the value of this Option.
		 * @return The value, of type T
		 */
		public T get() {
			T ret = null;
			try {
				Object o = this.getRawValue();
				if (o != null) {
					ret = this.klass.cast(o);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return ret;
		}
	}

	/**
	 * The application name
	 */
	private String _name;

	/**
	 * The argument vector.
	 */
	private String[] _argv;

	/**
	 * Remaining options that were held after the -- parameter.
	 */
	private ArrayList<String> _remaining;

	/**
	 * Maps, long and short
	 */
	private HashMap<String, OptionBase> _longMap, _shortMap;

	/**
	 * Whether this CommandLineParser has been parsed
	 */
	private boolean _parsed;

	/**
	 * Initializes a new CommandLineParser.
	 * @param args The argument vector.
	 */
	public CommandLine(String name, String[] args) {
		this._name = name;
		this._argv = args;
		this._remaining = new ArrayList<String>();
		this._longMap = new HashMap<String, OptionBase>();
		this._shortMap = new HashMap<String, OptionBase>();
		this._parsed = false;
	}

	/**
	 * Adds a new Option to this parser. Returns the receiver.
	 * @param opt The option to add
	 * @return The receiver
	 */
	public CommandLine add(OptionBase opt) {
		if (this._longMap.containsKey(opt.getLong())) {
			throw new RuntimeException(String.format("long option %s already registered", opt.getLong()));
		}
		this._longMap.put(opt.getLong(), opt);

		if (this._shortMap.containsKey(opt.getShort())) {
			throw new RuntimeException(String.format("short option %s already registered", opt.getShort()));
		}
		this._shortMap.put(opt.getShort(), opt);
		return this;
	}

	/**
	 * Parses command line arguments, returning the receiver.
	 * @return The receiver
	 */
	public CommandLine parse() {
		String key = null;
		OptionBase current = null;
		boolean parse = true, value = false;
		for (String s : this._argv) {
			if (parse) {
				if (value) {
					current.setValue(s);
					value = false;
				} else {
					if (s.startsWith("--")) {
						if (s.length() == 2) {
							parse = false;
						} else {
							key = s.substring(2).replace('-', '_');
							current = this._longMap.get(key);
						}
					} else if (s.startsWith("-")) {
						key = s.substring(1);
						current = this._shortMap.get(key);
					} else {
						throw new RuntimeException(String.format("not a valid option: %s", s));
					}

					if (current == null) {
						throw new RuntimeException(String.format("option %s doesn't exist", key));
					}

					if (current.isFlag()) {
						current.setValue(Boolean.TRUE.toString());
					} else {
						value = true;
					}
				}
			} else {
				this._remaining.add(s);
			}
		}

		this._parsed = true;
		return this;
	}

	/**
	 * Displays help to `out', returning the receiver.
	 * @param out The OutputStream
	 * @return The receiver
	 */
	public CommandLine help(OutputStream out) {
		PrintStream p = new PrintStream(out);
		ArrayList<OptionBase> all = new ArrayList<OptionBase>(this._longMap.values());
		Collections.sort(all);
		p.format("=== %s: %d possible arguments ===\n", this._name, all.size());
		for (OptionBase opt : all) {
			p.println(opt.toString());
		}
		return this;
	}

	/**
	 * Gets an Option from this CommandLineParser. Will raise a RuntimeError if the command line hasn't been parsed,
	 * or if the option is not found.
	 * @param klass The class
	 * @param key The key. Long or short form works.
	 * @return The Option
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> klass, String key) {
		return klass.cast(((Option<T>)this.getOption(key)).get());
	}

	/**
	 * Sets the value of an Option to value.
	 * @param klass The class
	 * @param key   The key
	 * @param value The value
	 * @return True if successful
	 */
	public <T> boolean put(Class<T> klass, String key, T value) {
		this.getOption(key).setValue(value == null ? null : value.toString());
		return true;
	}

	/**
	 * Sets the string value of an Option to value.
	 * @param klass The class
	 * @param key   The key
	 * @param value The value
	 * @return True if successful
	 */
	@SuppressWarnings("unchecked")
	public <T> boolean puts(Class<T> klass, String key, String value) {
		if (!value.isEmpty()) {
			T val = this.get(klass, key);
			Option<T> opt = (Option<T>)this.getOption(key);
			opt.setValue(value);
			try {
				opt.getRawValue();
			} catch (Exception e) {
				this.put(klass, key, val);
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	/**
	 * Returns whether this CommandLineParser has been parsed.
	 * @return Whether it's been parsed
	 */
	public boolean isParsed() {
		return this._parsed;
	}

	/**
	 * Returns the option for `key'.
	 * @param key The key
	 * @return The option
	 */
	private OptionBase getOption(String key) {
		if (!this.isParsed()) {
			throw new RuntimeException(String.format("command line not yet parsed"));
		}
		key = key.replace('-', '_');
		OptionBase opt = this._longMap.get(key);

		if (opt == null) {
			opt = this._shortMap.get(key);
		}

		if (opt == null) {
			throw new RuntimeException(String.format("invalid option: %s", key));
		}
		return opt;
	}
}
