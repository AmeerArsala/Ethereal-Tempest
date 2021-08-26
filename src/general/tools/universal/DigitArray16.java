/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.tools.universal;

/**
 *
 * @author night
 * 
 * Rules: 
 * The first digit must be a non-zero integer
 * The digits must all be positive or zero, however the int value as a whole can be negative
 * Up to 5 digits available (4 digits rule of thumb)
 */
public class DigitArray16 extends DigitArray<Short> {
    private short arr;
    
    public DigitArray16() {
        length = 0;
    }
    
    public DigitArray16(short digits) {
        arr = digits;
        length = countDigits(arr);
    }
    
    public DigitArray16(byte[] digits) {
        arr = toShort(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray16(short[] digits) {
        arr = toShort(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray16(int[] digits) {
        arr = toShort(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray16(long[] digits) {
        arr = toShort(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray16(DigitArray16 copy) {
        length = copy.length;
        
        if (length != 0) {
            arr = copy.arr;
        }
    }
    
    public short asShort() {
        return arr;
    }
    
    @Override
    public Short getNumber() {
        return arr;
    }
    
    //assumes isDeclared() == true
    @Override
    public byte getDigit(int index) {
        short num = arr;
        
        for (int i = 0; i <= index; i++) {
            short scalar = (short)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i == index) {
                return firstDigit;
            } else {
                //remove firstDigit
                num -= firstDigit * scalar;
            }
        }
        
        return -1;
    }
    
    //assumes isDeclared() == true
    @Override
    public byte[] getDigits(int indexA, int indexB) { // [indexA, indexB)
        byte[] digits = new byte[indexB - indexA];
        
        short num = arr;
        
        for (int i = 0; i < indexB; i++) {
            short scalar = (short)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i >= indexA) {
                digits[i - indexA] = firstDigit;
            }
            
            //remove firstDigit
            num -= firstDigit * scalar;
        }
        
        return digits;
    }
    
    //assumes isDeclared() == true
    @Override
    public short[] getShortDigits(int indexA, int indexB) { // [indexA, indexB)
        short[] digits = new short[indexB - indexA];
        
        short num = arr;
        
        for (int i = 0; i < indexB; i++) {
            short scalar = (short)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i >= indexA) {
                digits[i - indexA] = firstDigit;
            }
            
            //remove firstDigit
            num -= firstDigit * scalar;
        }
        
        return digits;
    }
    
    //assumes isDeclared() == true
    @Override
    public int[] getIntDigits(int indexA, int indexB) { // [indexA, indexB)
        int[] digits = new int[indexB - indexA];
        
        short num = arr;
        
        for (int i = 0; i < indexB; i++) {
            short scalar = (short)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i >= indexA) {
                digits[i - indexA] = firstDigit;
            }
            
            //remove firstDigit
            num -= firstDigit * scalar;
        }
        
        return digits;
    }
    
    //assumes isDeclared() == true
    @Override
    public long[] getLongDigits(int indexA, int indexB) { // [indexA, indexB)
        long[] digits = new long[indexB - indexA];
        
        short num = arr;
        
        for (int i = 0; i < indexB; i++) {
            short scalar = (short)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i >= indexA) {
                digits[i - indexA] = firstDigit;
            }
            
            //remove firstDigit
            num -= firstDigit * scalar;
        }
        
        return digits;
    }
    
    public short getDigitsAsShort(int i, int i1) {
        return toShort(getDigits(i, i1));
    }
    
    public short getDigitsAsShort(int i) {
        return toShort(getDigits(i));
    }
    
    @Override
    public DigitArray16 subArray(int i, int i1) {
        return new DigitArray16(getDigits(i, i1));
    }
    
    @Override
    public DigitArray16 subArray(int i) {
        return new DigitArray16(getDigits(i));
    }
    
    @Override
    public void setDigit(int i, byte digit) { //must be a single digit
        short scalar = (short)Math.pow(10, length - 1 - i); // correspondingDegree = length - 1 - i
        
        arr += (digit - getDigit(i)) * scalar;
    }
    
    //returns the new DigitArray length
    public byte setAllDigits(short digits) {
        arr = digits;
        return (length = countDigits(arr));
    }
    
    //returns the new DigitArray length
    public byte insertDigits(int i, short digits) {
        if (i == 0) {
            return unshift(digits);
        }
        
        if (i == length - 1) {
            return push(digits);
        }
        
        int correspondingFollowingDegree = length - 1 - (i + 1);
        short followingScalar = (short)Math.pow(10, correspondingFollowingDegree);
        short followingDigits = (short)(arr - ((arr / followingScalar) * followingScalar));
        
        short tailDigits = joinDigits(digits, followingDigits);
        return push(tailDigits);
    }
    
    //adds digits to the end, returns the new DigitArray length
    public byte push(short digits) {
        if (length == 0) {
            return setAllDigits(digits);
        } else {
            int additionalDigitCount = countDigits(digits);
            arr = (short)((arr * ((short)Math.pow(10, additionalDigitCount))) + digits);
            
            return (length += additionalDigitCount);
        }
    }
    
    //adds digits to the beginning, returns the new DigitArray length
    public byte unshift(short digits) {
        if (length == 0) {
            return setAllDigits(digits);
        } else {
            arr = (short)((digits * ((short)Math.pow(10, length - 1))) + arr);
            
            return (length += countDigits(digits));
        }
    }
    
    //removes the last digit and returns it
    @Override
    public byte pop() {
        byte removedDigit = (byte)(arr - ((arr / 10) * 10));
        arr /= 10;
        --length;
        return removedDigit;
    }
    
    //removes the first digit and returns it
    @Override
    public byte shift() {
        short scalar = (short)Math.pow(10, length - 1);
        byte firstDigit = (byte)(arr / scalar);
        
        arr -= firstDigit * scalar;
        --length;
        return firstDigit;
    }
    
    @Override
    public void removeAll(int i, int i1) {
        int numDigitsToRemove = i1 - i;
        if (numDigitsToRemove == 1 && i1 == length) { //if only last value
            pop();
            return;
        }
        
        short part1 = getDigitsAsShort(0, i);
        short part2 = getDigitsAsShort(i1);
        
        arr = joinDigits(part1, part2);
        length -= numDigitsToRemove;
    }
    
    
    //optimized to-the-point log10 for bytes, shorts, ints, longs
    public static byte countDigits(short digits) {
        byte digitCount = 1; //at least 1 digit
        short num = (short)(digits / 10);
        while (num != 0) {
            num /= 10;
            ++digitCount;
        }
        
        return digitCount;
    }
    
    public static short joinDigits(short digitsA, short digitsB) {
        byte additionalDigitCount = countDigits(digitsB);
        return (short)((digitsA * ((short)Math.pow(10, additionalDigitCount))) + digitsB);
    }
}
