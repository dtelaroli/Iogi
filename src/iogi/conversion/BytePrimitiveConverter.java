package iogi.conversion;

import iogi.reflection.Target;

public class BytePrimitiveConverter extends TypeConverter<Byte> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == byte.class;
	}

	@Override
	protected Byte convert(String stringValue, Target<?> to) {
		return new ByteWrapperConverter().convert(stringValue, to);
	}

}
