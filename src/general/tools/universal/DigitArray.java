/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.tools.universal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author night
 * @param <N> any number wrapper
 */
public abstract class DigitArray<N extends Number> {
    protected byte length; //length won't be more than 19, therefore byte (goes up to 127)
    
    protected DigitArray() {
        length = 0;
    }
    
    public boolean isDeclared() {
        return length != 0;
    }
    
    public byte getLength() {
        return length;
    }
    
    public abstract N getNumber();
    
    public abstract byte getDigit(int index);
    
    public abstract byte[] getDigits(int i, int i1);
    public abstract short[] getShortDigits(int i, int i1);
    public abstract int[] getIntDigits(int i, int i1);
    public abstract long[] getLongDigits(int i, int i1);
    
    public byte[] getDigits(int i) {
        return getDigits(i, length);
    }
    
    public short[] getShortDigits(int i) {
        return getShortDigits(i, length);
    }
    
    public int[] getIntDigits(int i) {
        return getIntDigits(i, length);
    }
    
    public long[] getLongDigits(int i) {
        return getLongDigits(i, length);
    }
    
    public abstract DigitArray subArray(int i, int i1);
    public abstract DigitArray subArray(int i);
    
    public abstract void setDigit(int i, byte digit);
    
    //removes the last digit and returns it
    public abstract byte pop();
    
    //removes the first digit and returns it
    public abstract byte shift();
    
    //removes specified digit and returns it
    public byte remove(int i) {
        byte digit = getDigit(i);
        
        removeAll(i, i + 1);
        
        return digit;
    }
    
    public abstract void removeAll(int i, int i1);
    
    public void removeAll(int i) {
        removeAll(i, length);
    }
    
    public byte[] toArray() {
        return getDigits(0, length);
    }
    
    public short[] toShortArray() {
        return getShortDigits(0, length);
    }
    
    public int[] toIntArray() {
        return getIntDigits(0, length);
    }
    
    public long[] toLongArray() {
        return getLongDigits(0, length);
    }
    
    public Byte[] toByteArray() {
        byte[] digitArray = toArray();
        Byte[] array = new Byte[digitArray.length];
        
        for (int i = 0; i < digitArray.length; i++) {
            array[i] = digitArray[i];
        }
        
        return array;
    }
    
    public Short[] toShortWrapperArray() {
        short[] digitArray = toShortArray();
        Short[] array = new Short[digitArray.length];
        
        for (int i = 0; i < digitArray.length; i++) {
            array[i] = digitArray[i];
        }
        
        return array;
    }
    
    public Integer[] toIntegerArray() {
        int[] digitArray = toIntArray();
        Integer[] array = new Integer[digitArray.length];
        
        for (int i = 0; i < digitArray.length; i++) {
            array[i] = digitArray[i];
        }
        
        return array;
    }
    
    public Long[] toLongWrapperArray() {
        long[] digitArray = toLongArray();
        Long[] array = new Long[digitArray.length];
        
        for (int i = 0; i < digitArray.length; i++) {
            array[i] = digitArray[i];
        }
        
        return array;
    }
    
    //makes a new ArrayList if store == null
    public List<Byte> toList(List<Byte> store) {
        byte[] array = toArray();
        List<Byte> list = store != null ? store : new ArrayList<>();
        
        for (byte digit : array) {
            list.add(digit);
        }
        
        return list;
    }
    
    //makes a new ArrayList if store == null
    public List<Short> toShortList(List<Short> store) {
        short[] array = toShortArray();
        List<Short> list = store != null ? store : new ArrayList<>();
        
        for (short digit : array) {
            list.add(digit);
        }
        
        return list;
    }
    
    //makes a new ArrayList if store == null
    public List<Integer> toIntegerList(List<Integer> store) {
        int[] array = toIntArray();
        List<Integer> list = store != null ? store : new ArrayList<>();
        
        for (int digit : array) {
            list.add(digit);
        }
        
        return list;
    }
    
    //makes a new ArrayList if store == null
    public List<Long> toLongList(List<Long> store) {
        long[] array = toLongArray();
        List<Long> list = store != null ? store : new ArrayList<>();
        
        for (long digit : array) {
            list.add(digit);
        }
        
        return list;
    }
    
    public List<Byte> toReadOnlyList() {
        return Arrays.asList(toByteArray());
    }
    
    public List<Short> toReadOnlyShortList() {
        return Arrays.asList(toShortWrapperArray());
    }
    
    public List<Integer> toReadOnlyIntegerList() {
        return Arrays.asList(toIntegerArray());
    }
    
    public List<Long> toReadOnlyLongList() {
        return Arrays.asList(toLongWrapperArray());
    }
    
    
    public static byte toByte(byte[] digits) {
        byte all = 0;
        for (int i = 0; i < digits.length; i++) {
            byte scalar = (byte)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static byte toByte(short[] digits) {
        byte all = 0;
        for (int i = 0; i < digits.length; i++) {
            byte scalar = (byte)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static byte toByte(int[] digits) {
        byte all = 0;
        for (int i = 0; i < digits.length; i++) {
            byte scalar = (byte)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static byte toByte(long[] digits) {
        byte all = 0;
        for (int i = 0; i < digits.length; i++) {
            byte scalar = (byte)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static short toShort(byte[] digits) {
        short all = 0;
        for (int i = 0; i < digits.length; i++) {
            short scalar = (short)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static short toShort(short[] digits) {
        short all = 0;
        for (int i = 0; i < digits.length; i++) {
            short scalar = (short)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static short toShort(int[] digits) {
        short all = 0;
        for (int i = 0; i < digits.length; i++) {
            short scalar = (short)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static short toShort(long[] digits) {
        short all = 0;
        for (int i = 0; i < digits.length; i++) {
            short scalar = (short)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static int toInt(byte[] digits) {
        int all = 0;
        for (int i = 0; i < digits.length; i++) {
            int scalar = (int)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static int toInt(short[] digits) {
        int all = 0;
        for (int i = 0; i < digits.length; i++) {
            int scalar = (int)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static int toInt(int[] digits) {
        int all = 0;
        for (int i = 0; i < digits.length; i++) {
            int scalar = (int)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static int toInt(long[] digits) {
        int all = 0;
        for (int i = 0; i < digits.length; i++) {
            int scalar = (int)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static long toLong(byte[] digits) {
        long all = 0l;
        for (int i = 0; i < digits.length; i++) {
            long scalar = (long)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static long toLong(short[] digits) {
        long all = 0l;
        for (int i = 0; i < digits.length; i++) {
            long scalar = (long)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static long toLong(int[] digits) {
        long all = 0l;
        for (int i = 0; i < digits.length; i++) {
            long scalar = (long)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static long toLong(long[] digits) {
        long all = 0l;
        for (int i = 0; i < digits.length; i++) {
            long scalar = (long)Math.pow(10, digits.length - 1 - i); // correspondingDegree = length - 1 - i
            all += digits[i] * scalar;
        }
        
        return all;
    }
    
    public static DigitArray autoCreate(int maxDigits) {
        if (maxDigits <= 3) {
            return new DigitArray8();
        } 
        
        if (maxDigits <= 5) {
            return new DigitArray16();
        } 
        
        if (maxDigits <= 9) {
            return new DigitArray32();
        }
        
        return new DigitArray64();
    }
    
    public static DigitArray autoCreate(int maxDigits, int digits) {
        if (maxDigits <= 3) {
            return new DigitArray8((byte)digits);
        } 
        
        if (maxDigits <= 5) {
            return new DigitArray16((short)digits);
        } 
        
        if (maxDigits <= 9) {
            return new DigitArray32(digits);
        }
        
        return new DigitArray64(digits);
    }
    
    public static DigitArray autoCreate(int maxDigits, byte[] digits) {
        if (maxDigits <= 3) {
            return new DigitArray8(digits);
        } 
        
        if (maxDigits <= 5) {
            return new DigitArray16(digits);
        } 
        
        if (maxDigits <= 9) {
            return new DigitArray32(digits);
        }
        
        return new DigitArray64(digits);
    }
    
    public static DigitArray autoCreate(int maxDigits, short[] digits) {
        if (maxDigits <= 3) {
            return new DigitArray8(digits);
        } 
        
        if (maxDigits <= 5) {
            return new DigitArray16(digits);
        } 
        
        if (maxDigits <= 9) {
            return new DigitArray32(digits);
        }
        
        return new DigitArray64(digits);
    }
    
    public static DigitArray autoCreate(int maxDigits, int[] digits) {
        if (maxDigits <= 3) {
            return new DigitArray8(digits);
        } 
        
        if (maxDigits <= 5) {
            return new DigitArray16(digits);
        } 
        
        if (maxDigits <= 9) {
            return new DigitArray32(digits);
        }
        
        return new DigitArray64(digits);
    }
    
    public static DigitArray autoCreate(int maxDigits, long[] digits) {
        if (maxDigits <= 3) {
            return new DigitArray8(digits);
        } 
        
        if (maxDigits <= 5) {
            return new DigitArray16(digits);
        } 
        
        if (maxDigits <= 9) {
            return new DigitArray32(digits);
        }
        
        return new DigitArray64(digits);
    }
}
