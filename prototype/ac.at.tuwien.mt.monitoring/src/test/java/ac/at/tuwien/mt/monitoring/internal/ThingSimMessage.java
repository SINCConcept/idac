/*
 * Copyright (c) 2016. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS".
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
 * 
 * Author: Florin Bogdan Balint
 * 
 */
package ac.at.tuwien.mt.monitoring.internal;

import java.io.Serializable;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class ThingSimMessage implements Serializable {

	private String c0;
	private String c1;
	private String c2;
	private String c3;
	private String c4;
	private String c5;
	private String c6;
	private String c7;
	private String c8;
	private String c9;

	private String c10;
	private String c11;
	private String c12;
	private String c13;
	private String c14;
	private String c15;
	private String c16;
	private String c17;
	private String c18;
	private String c19;

	private String c20;
	private String c21;
	private String c22;
	private String c23;

	public ThingSimMessage() {
		// empty constructor
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("c0", c0);
		document.put("c1", c1);
		document.put("c2", c2);
		document.put("c3", c3);
		document.put("c4", c4);
		document.put("c5", c5);
		document.put("c6", c6);
		document.put("c7", c7);
		document.put("c8", c8);
		document.put("c9", c9);
		document.put("c10", c10);
		document.put("c11", c11);
		document.put("c12", c12);
		document.put("c13", c13);
		document.put("c14", c14);
		document.put("c15", c15);
		document.put("c16", c16);
		document.put("c17", c17);
		document.put("c18", c18);
		document.put("c19", c19);
		document.put("c20", c20);
		document.put("c21", c21);
		document.put("c22", c22);
		document.put("c23", c23);
		return document;
	}

	/**
	 * @return the c0
	 */
	public String getC0() {
		return c0;
	}

	/**
	 * @param c0
	 *            the c0 to set
	 */
	public void setC0(String c0) {
		this.c0 = c0;
	}

	/**
	 * @return the c1
	 */
	public String getC1() {
		return c1;
	}

	/**
	 * @param c1
	 *            the c1 to set
	 */
	public void setC1(String c1) {
		this.c1 = c1;
	}

	/**
	 * @return the c2
	 */
	public String getC2() {
		return c2;
	}

	/**
	 * @param c2
	 *            the c2 to set
	 */
	public void setC2(String c2) {
		this.c2 = c2;
	}

	/**
	 * @return the c3
	 */
	public String getC3() {
		return c3;
	}

	/**
	 * @param c3
	 *            the c3 to set
	 */
	public void setC3(String c3) {
		this.c3 = c3;
	}

	/**
	 * @return the c4
	 */
	public String getC4() {
		return c4;
	}

	/**
	 * @param c4
	 *            the c4 to set
	 */
	public void setC4(String c4) {
		this.c4 = c4;
	}

	/**
	 * @return the c5
	 */
	public String getC5() {
		return c5;
	}

	/**
	 * @param c5
	 *            the c5 to set
	 */
	public void setC5(String c5) {
		this.c5 = c5;
	}

	/**
	 * @return the c6
	 */
	public String getC6() {
		return c6;
	}

	/**
	 * @param c6
	 *            the c6 to set
	 */
	public void setC6(String c6) {
		this.c6 = c6;
	}

	/**
	 * @return the c7
	 */
	public String getC7() {
		return c7;
	}

	/**
	 * @param c7
	 *            the c7 to set
	 */
	public void setC7(String c7) {
		this.c7 = c7;
	}

	/**
	 * @return the c8
	 */
	public String getC8() {
		return c8;
	}

	/**
	 * @param c8
	 *            the c8 to set
	 */
	public void setC8(String c8) {
		this.c8 = c8;
	}

	/**
	 * @return the c9
	 */
	public String getC9() {
		return c9;
	}

	/**
	 * @param c9
	 *            the c9 to set
	 */
	public void setC9(String c9) {
		this.c9 = c9;
	}

	/**
	 * @return the c10
	 */
	public String getC10() {
		return c10;
	}

	/**
	 * @param c10
	 *            the c10 to set
	 */
	public void setC10(String c10) {
		this.c10 = c10;
	}

	/**
	 * @return the c11
	 */
	public String getC11() {
		return c11;
	}

	/**
	 * @param c11
	 *            the c11 to set
	 */
	public void setC11(String c11) {
		this.c11 = c11;
	}

	/**
	 * @return the c12
	 */
	public String getC12() {
		return c12;
	}

	/**
	 * @param c12
	 *            the c12 to set
	 */
	public void setC12(String c12) {
		this.c12 = c12;
	}

	/**
	 * @return the c13
	 */
	public String getC13() {
		return c13;
	}

	/**
	 * @param c13
	 *            the c13 to set
	 */
	public void setC13(String c13) {
		this.c13 = c13;
	}

	/**
	 * @return the c14
	 */
	public String getC14() {
		return c14;
	}

	/**
	 * @param c14
	 *            the c14 to set
	 */
	public void setC14(String c14) {
		this.c14 = c14;
	}

	/**
	 * @return the c15
	 */
	public String getC15() {
		return c15;
	}

	/**
	 * @param c15
	 *            the c15 to set
	 */
	public void setC15(String c15) {
		this.c15 = c15;
	}

	/**
	 * @return the c16
	 */
	public String getC16() {
		return c16;
	}

	/**
	 * @param c16
	 *            the c16 to set
	 */
	public void setC16(String c16) {
		this.c16 = c16;
	}

	/**
	 * @return the c17
	 */
	public String getC17() {
		return c17;
	}

	/**
	 * @param c17
	 *            the c17 to set
	 */
	public void setC17(String c17) {
		this.c17 = c17;
	}

	/**
	 * @return the c18
	 */
	public String getC18() {
		return c18;
	}

	/**
	 * @param c18
	 *            the c18 to set
	 */
	public void setC18(String c18) {
		this.c18 = c18;
	}

	/**
	 * @return the c19
	 */
	public String getC19() {
		return c19;
	}

	/**
	 * @param c19
	 *            the c19 to set
	 */
	public void setC19(String c19) {
		this.c19 = c19;
	}

	/**
	 * @return the c20
	 */
	public String getC20() {
		return c20;
	}

	/**
	 * @param c20
	 *            the c20 to set
	 */
	public void setC20(String c20) {
		this.c20 = c20;
	}

	/**
	 * @return the c21
	 */
	public String getC21() {
		return c21;
	}

	/**
	 * @param c21
	 *            the c21 to set
	 */
	public void setC21(String c21) {
		this.c21 = c21;
	}

	/**
	 * @return the c22
	 */
	public String getC22() {
		return c22;
	}

	/**
	 * @param c22
	 *            the c22 to set
	 */
	public void setC22(String c22) {
		this.c22 = c22;
	}

	/**
	 * @return the c23
	 */
	public String getC23() {
		return c23;
	}

	/**
	 * @param c23
	 *            the c23 to set
	 */
	public void setC23(String c23) {
		this.c23 = c23;
	}

}
