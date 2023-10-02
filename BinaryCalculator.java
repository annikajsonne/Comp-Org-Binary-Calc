/**
 * Class with methods to implement the basic arithmetic operations by operating
 * on bit fields.
 *
 * This is the skeleton code form COMP2691 Assignment #2.
 * 
 * create helper methods for absolute value, lsl, rsl etc
 */
public class BinaryCalculator {
	public static BitField add(BitField a, BitField b) {
		// create a new bitfield inside every helper method of the right size, and then
		// adjust the new one appropriately, and dont mess with the one that is passed
		// in

		if (null == a || null == b || a.size() != b.size()) {
			throw new IllegalArgumentException(
					"BinaryCalculator.add(a,b): a and b cannot be null and must be the same length.");
		}

		BitField out = new BitField(a.size());
		boolean carry = false;
		// iterate through addends, update carry and result appropriately
		for (int i = 0; i < a.size(); i++) {
			if (a.get(i) == false && b.get(i) == false && carry == false) {
				// if the bits at index i are all equal to 0 and the carry field is 0:
				out.set(i, false);
				carry = false;
			} else if (a.get(i) == true && b.get(i) == false && carry == false
					|| a.get(i) == false && b.get(i) == true && carry == false
					|| a.get(i) == false && b.get(i) == false && carry == true) {
				// if one of the bits of a or b at index i is equal to 1 and other is equal to
				// 0, or both are 0 and carry field is 1:
				out.set(i, true);
				carry = false;
			} else if (a.get(i) == true && b.get(i) == true && carry == false
					|| a.get(i) == false && b.get(i) == true && carry == true
					|| a.get(i) == true && b.get(i) == false && carry == true) {
				// if both of the bits at index i are equal to 1, or one is equal to 1 and the
				// carry field is 1:
				out.set(i, false);
				carry = true;
			} else {
				// if both of the bits at index i are equal to 1 and the carry field is equal to
				// 1:
				out.set(i, true);
				carry = true;
			}

		}
		return out;
	}

	public static BitField subtract(BitField a, BitField b) {
		if (null == a || null == b || a.size() != b.size()) {
			throw new IllegalArgumentException(
					"BinaryCalculator.subtract(a,b): a and b cannot be null and must be the same length.");
		}
		//flip the sign of b and then add a and b, therefore imitating a subtraction
		BitField out = new BitField(a.size());
		out = flipSign(b);
		out = add(a, out);
		return out;
	}

	public static BitField multiply(BitField a, BitField b) {
		if (null == a || null == b || a.size() != b.size()) {
			throw new IllegalArgumentException(
					"BinaryCalculator.multiply(a,b): a and b cannot be null and must be the same length.");
		}
		BitField multiplier = a.copy();
		BitField multiplicand = b.copy();
		BitField product = new BitField(a.size());
		
		// finding the sign for the final product
		boolean finalSign = false;
		if (multiplier.getMSB() == multiplicand.getMSB()) {
			finalSign = false;
		} else {
			finalSign = true;
		}

		//multiplication step
		for (int i = 0; i < a.size(); i++) {
			if (multiplier.getLSB() == true) {
				product = add(product, multiplicand); // product = product + multiplicand
			}
			multiplicand = leftShift(multiplicand, 1); // shift left multiplicand
			multiplier = rightShift(multiplier, 1); // shift right multiplicand
		}

		// negating the final result if needed
		if (finalSign = false) {
			product.set(product.size() - 1, true);
		}
		return product;
	}

	public static BitField[] divide(BitField a, BitField b) { // worry abt signed bit
		if (null == a || null == b || a.size() != b.size()) {
			throw new IllegalArgumentException(
					"BinaryCalculator.divide(a,b): a and b cannot be null and must be the same length.");
		}
		// figure out if the divisor is 0
		boolean z = true;
		for (int i = 0; i < b.size(); i++) {
			if (b.get(i) == true) {
				z = false;
				break;
			}
		}
		// if the divisor is 0, return null
		if (z) {
			return null;
		}

		// finding signs for the quotient and the remainder
		boolean remSign = a.getMSB(); // if true = negative
		boolean quotientSign = false;
		if (a.getMSB() == b.getMSB()) {
			quotientSign = false;
		} else {
			quotientSign = true;
		}

		// set up the dividend as the absolute value of a - and set it to the remainder
		// to start
		BitField dividend = absValue(a);
		BitField remainder2x = new BitField(dividend.size() * 2);
		for (int i = 0; i < dividend.size(); i++) {
			remainder2x.set(i, dividend.get(i));
		}

		// set up the divisor as the absolute value of b, double the size, and in the
		// left half of the bits
		BitField absVdivisor = absValue(b);
		BitField divisor = new BitField(absVdivisor.size() * 2);
		for (int i = 0; i < b.size(); i++) {
			divisor.set(i + b.size(), absVdivisor.get(i));
		}

		//set up quotient to be 0 with the regular size of bits
		BitField quotient = new BitField(a.size());

		// doing the division
		for (int i = 0; i < a.size() + 1; i++) {
			remainder2x = subtract(remainder2x, divisor); // remainder = remainder - divisor
			if (remainder2x.getMSB() == true) { // if the remainder is negative
				remainder2x = add(remainder2x, divisor);
				quotient = leftShift(quotient, 1);
				quotient.set(0, false);
			} else { // if the remainder is positive
				quotient = leftShift(quotient, 1);
				quotient.set(0, true);
			}
			divisor = rightShift(divisor, 1);
		}

		// cutting the number of bits of the remainder to the original size
		BitField remainder = new BitField(quotient.size());
		for (int i = 0; i < remainder.size(); i++) {
			remainder.set(i, remainder2x.get(i));
		}
		// negating the remainder if the signed bits differed
		if (remSign) {
			remainder = flipSign(remainder);
		}
		// negating the final quotient if the signed bits differed
		if (quotientSign) {
			quotient = flipSign(quotient);
		}
		// Return both the quotient and the remainder
		BitField[] out = new BitField[2];
		out[0] = quotient; // quotient
		out[1] = remainder; // remainder
		return out;
	}

	public static BitField leftShift(BitField a, int value) {
		//shifts all the values left by one and fills the LSB in as 0
		BitField out = new BitField(a.size());
		for (int i = value; i < a.size(); i++) {
			out.set(i, a.get(i - value));
		}
		return out;
	}

	public static BitField rightShift(BitField a, int value) {
		//shifts all the values right by one and fills the MSB in as 0
		BitField out = new BitField(a.size());
		for (int i = a.size() - 1 - value; i >= 0; i--) {
			out.set(i, a.get(i + value));
		}
		return out;
	}

	public static BitField absValue(BitField a) {
		//if the binary number is negative/msb is true then flip the sign
		BitField out = a.copy();
		if (out.getMSB()) {
			out = flipSign(out);
		}
		return out;
	}

	public static BitField flipSign(BitField a) {
		//uses the flipbits method and then adds one to return the twos complement
		BitField temp = flipBits(a);
		BitField one = new BitField(a.size());
		one.set(0, true);
		temp = add(temp, one);
		return temp;
	}

	public static BitField flipBits(BitField a) {
		//flips the bits of the binary number
		BitField out = new BitField(a.size());
		for (int i = 0; i < a.size(); i++) {
			out.set(i, !a.get(i));
		}
		return out;
	}

}
