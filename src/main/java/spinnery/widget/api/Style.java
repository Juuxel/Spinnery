package spinnery.widget.api;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import io.github.cottonmc.jankson.JanksonOps;
import net.minecraft.util.ResourceLocation;
import spinnery.Spinnery;
import spinnery.widget.WAbstractWidget;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class Style {
	protected static Map<Class<?>, Function<?, JsonElement>> jsonSerializers = new HashMap<>();

	static {
		registerSerializer(Number.class, JanksonOps.INSTANCE::createNumeric);
		registerSerializer(String.class, JanksonOps.INSTANCE::createString);
		registerSerializer(Boolean.class, JanksonOps.INSTANCE::createBoolean);
		registerSerializer(Position.class, v -> JanksonOps.INSTANCE.createIntList(IntStream.of(v.x, v.y, v.z)));
		registerSerializer(Size.class, v -> JanksonOps.INSTANCE.createIntList(IntStream.of(v.getWidth(), v.getHeight())));
		registerSerializer(Color.class, v -> JanksonOps.INSTANCE.createLong(v.ARGB));
		// TODO: sided size serialization
	}

	protected final Map<String, JsonElement> properties = new HashMap<>();

	public Style(Map<String, JsonElement> properties) {
		this.properties.putAll(properties);
	}

	public Style() {
	}

	public static Style of(Style other) {
		return new Style(other.properties);
	}

	// GETTERS

	protected static <T> void registerSerializer(Class<T> vClass, Function<T, JsonElement> serializer) {
		jsonSerializers.put(vClass, serializer);
	}

	public boolean contains(String key) {
		return properties.get(key) != null;
	}

	public boolean asBoolean(String property) {
		return JanksonOps.INSTANCE.getNumberValue(getElement(property)).orElse(0).intValue() == 1;
	}

	protected JsonElement getElement(String key) {
		return properties.get(key);
	}

	public int asInt(String property) {
		return asNumber(property).intValue();
	}

	protected Number asNumber(String property) {
		return JanksonOps.INSTANCE.getNumberValue(getElement(property)).orElse(0);
	}

	public long asLong(String property) {
		return asNumber(property).longValue();
	}

	public float asFloat(String property) {
		return asNumber(property).floatValue();
	}

	public double asDouble(String property) {
		return asNumber(property).doubleValue();
	}

	public Color asColor(String property) {
		return asColor(property, Color.of("0xff000000"));
	}

	public Color asColor(String property, Color defaultColor) {
		return JanksonOps.INSTANCE.getNumberValue(getElement(property))
				.map(Color::of).orElse(defaultColor);
	}

	public Size asSize(String property) {
		JsonElement el = getElement(property);
		if (!(el instanceof JsonArray)) return Size.of(0, 0);
		JsonArray array = (JsonArray) el;
		return Size.of(array.getInt(0, 0), array.getInt(1, 0));
	}

	// Clockwise from top
	public Padding asPadding(String property) {
		JsonElement el = getElement(property);
		Optional<Number> singleValue = JanksonOps.INSTANCE.getNumberValue(el);
		if (singleValue.isPresent()) {
			int intValue = singleValue.get().intValue();
			Size size = Size.of(intValue, intValue);
			return Padding.of(intValue);
		}

		if (!(el instanceof JsonArray)) return Padding.of(0);
		JsonArray array = (JsonArray) el;

		if (array.size() == 1) {
			return Padding.of(array.getInt(0, 0));
		} else if (array.size() == 2) {
			return Padding.of(array.getInt(0, 0), array.getInt(1, 0));
		} else if (array.size() >= 4) {
			return Padding.of(array.getInt(0, 0), array.getInt(1, 0), array.getInt(2, 0), array.getInt(3, 0));
		}
		return Padding.of(0);
	}

	public Position asPosition(String property) {
		JsonElement el = getElement(property);
		if (!(el instanceof JsonArray)) return Position.origin();
		JsonArray array = (JsonArray) el;
		return Position.of(array.getInt(0, 0), array.getInt(1, 0), array.getInt(2, 0));
	}

	// SETTERS

	public Position asAnchoredPosition(String property, WAbstractWidget anchor) {
		JsonElement el = getElement(property);
		if (!(el instanceof JsonArray)) return Position.of(anchor);
		JsonArray array = (JsonArray) el;
		return Position.of(anchor, array.getInt(0, 0), array.getInt(1, 0), array.getInt(2, 0));
	}

	public ResourceLocation asResourceLocation(String property) {
		return new ResourceLocation(asString(property));
	}

	public String asString(String property) {
		return JanksonOps.INSTANCE.getStringValue(getElement(property)).orElse("");
	}

	public <T> Style override(String property, T value) {
		Function<T, JsonElement> ser = getSerializer(value);
		if (ser != null) {
			properties.put(property, ser.apply(value));
		} else {
			Spinnery.LOGGER.warn("Failed to override {}: themes do not support values of class {}",
					property, value.getClass().getSimpleName());
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	protected static <T> Function<T, JsonElement> getSerializer(T value) {
		for (Class<?> serClass : jsonSerializers.keySet()) {
			if (serClass.isAssignableFrom(value.getClass())) {
				return (Function<T, JsonElement>) jsonSerializers.get(serClass);
			}
		}
		return null;
	}

	public Style mergeFrom(Style other) {
		this.properties.putAll(other.properties);
		return this;
	}
}
