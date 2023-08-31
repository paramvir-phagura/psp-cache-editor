package net.openrs.cache.def;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import net.openrs.cache.Cache;
import net.openrs.util.ByteBufferUtils;

public class RSInterface {

	private static HashMap<Integer, RSInterface> interfaces = new HashMap<Integer, RSInterface>();

	public ArrayList<RSInterface> children = new ArrayList<RSInterface>();

	static int anInt717;
	protected int anInt718;
	protected Object[] anObjectArray719;
	public int spriteId;
	protected int anInt721;
	public int anInt722;
	private short[] aShortArray723;
	protected boolean aBoolean724;
	public int xPos;
	protected int[] anIntArray726;
	protected Object[] anObjectArray727;
	static int anInt728;
	protected int anInt729;
	protected int anInt730;
	protected int anInt731;
	public int height;
	protected int anInt733;
	protected Object[] anObjectArray734;
	protected Object[] anObjectArray735;
	public int someInt = 0;
	protected Object[] anObjectArray737;
	protected byte[] aByteArray738;
	protected int anInt739;
	protected int anInt740;
	protected int anInt741;
	public boolean filled;
	static int anInt743;
	protected Object[] anObjectArray744;
	protected int anInt745;
	protected Object[] anObjectArray746;
	protected Object[] anObjectArray747;
	protected Object[] anObjectArray748;
	protected int anInt749;
	protected Object[] anObjectArray750;
	static int anInt751;
	protected boolean aBoolean752;
	protected byte[] aByteArray753;
	protected Object[] anObjectArray754;
	public int anInt755;
	protected int anInt756;
	protected Object[] anObjectArray757;
	protected int anInt758;
	protected boolean aBoolean759;
	protected int anInt760;
	protected int anInt761;
	public int anInt762;
	protected Object[] anObjectArray763;
	protected int anInt764;
	static int anInt765;
	static int anInt766;
	protected int anInt767;
	protected int anInt768;
	protected int anInt769;
	protected int anInt770;
	protected boolean aBoolean771;
	protected Object[] anObjectArray772;
	protected int[] anIntArray773;
	protected Object[] anObjectArray774;
	static int anInt775;
	protected int anInt776;
	protected int anInt777;
	public int anInt778;
	protected Object[] anObjectArray779;
	protected int anInt780;
	public String aString781;
	public int width;
	protected boolean aBoolean783;
	static int anInt784;
	protected int anInt785;
	protected Object[] anObjectArray786;
	protected RSInterface[] aClass51Array787;
	protected Object[] anObjectArray788;
	protected Object[] anObjectArray789;
	protected Object[] anObjectArray790;
	protected int anInt791;
	//private Class262 aClass262_792;
	protected Object[] anObjectArray793;
	protected int anInt794;
	protected boolean aBoolean795;
	protected boolean aBoolean796;
	protected int anInt797;
	protected Object[] anObjectArray798;
	protected boolean aBoolean799;
	public int fontId;
	public byte widthLayout;
	protected int anInt802;
	//protected Class44 aClass44_803;
	protected RSInterface[] aClass51Array804;
	public boolean aBoolean805;
	protected Object[] anObjectArray806;
	public int anInt807;
	public int anInt808;
	protected int anInt809;
	public int anInt810;
	protected boolean aBoolean811;
	protected Object[] anObjectArray812;
	protected int[] anIntArray813;
	protected boolean aBoolean814;
	public int anInt815;
	//protected Node_Sub30 aNode_Sub30_816;
	protected Object[] anObjectArray817;
	protected int[] anIntArray818;
	protected Object[] anObjectArray819;
	protected int anInt820;
	static int anInt821;
	protected int[] anIntArray822;
	static int anInt823;
	protected int anInt824;
	static int anInt825;
	static int anInt826;
	private short[] aShortArray827;
	protected int anInt828;
	protected int anInt829;
	protected String aString830;
	public int screenWidth;
	protected int anInt832;
	protected int anInt833;
	protected int anInt834;
	protected Object[] anObjectArray835;
	protected Object[] anObjectArray836;
	static int anInt837;
	protected Object[] anObjectArray838;
	protected Object[] anObjectArray839;
	static int anInt840;
	private short[] aShortArray841;
	protected String[] aStringArray842;
	static int anInt843;
	protected int anInt844;
	public int anInt845;
	static int anInt846;
	protected int anInt847;
	protected int anInt848;
	protected int anInt849;
	protected boolean aBoolean850;
	protected Object[] anObjectArray851;
	public byte heightOffset;
	protected int anInt853;
	protected Object[] anObjectArray854;
	public int parentId;
	protected int[] anIntArray856;
	public int anInt857;
	public int anInt858;
	protected String aString859;
	public int contentType;
	public byte heightLayout;
	public int anInt862;
	protected int anInt863;
	protected RSInterface aClass51_864;
	static int anInt865;
	protected Object[] anObjectArray866;
	public int yPos;
	static int anInt868;
	protected boolean aBoolean869;
	protected int anInt870;
	protected int anInt871;
	protected boolean aBoolean872;
	public int anInt873;
	protected int[] anIntArray874;
	private short[] aShortArray875;
	protected boolean aBoolean876;
	protected boolean aBoolean877;
	protected int[] anIntArray878;
	protected String aString879;
	static int anInt880;
	public int anInt881;
	protected int anInt882;
	public int screenHeight;
	protected int anInt884;
	protected int[] anIntArray885;
	public byte widthOffset;
	protected Object[] anObjectArray887;
	protected String aString888;
	//protected Class336_Sub1 aClass336_Sub1_889;
	static int anInt890;
	public boolean aBoolean891;
	static int anInt892;
	protected boolean aBoolean893;
	static int anInt894;
	protected int anInt895;
	protected int anInt896;


	private final int[] method622(byte b, ByteBuffer buffer) throws IOException {

		anInt843++;
		int i = buffer.get();
		if (i == 0) {
			return null;
		}
		int[] is = new int[i];
		for (int i_3_ = 0; i > i_3_; i_3_++)
			is[i_3_] = buffer.getInt();
		return is;
	}

	private final Object[] method623(byte b, ByteBuffer buffer) throws IOException {
		anInt865++;
		int i = buffer.get();
		if (i == 0) {
			return null;
		}
		Object[] objects = new Object[i];
		for (int i_4_ = 0; i_4_ < i; i_4_++) {
			int i_5_ = buffer.get();
			if (i_5_ == 0) {
				objects[i_4_] = new Integer(buffer.getInt());
			} else if (i_5_ == 1) {
				objects[i_4_] = ByteBufferUtils.getJagexString(buffer);
			}
		}
		if (b != -82) {
			return null;
		}
		aBoolean796 = true;
		return objects;
	}



	static final int method629(boolean bool, int i) {
		anInt840++;
		if (bool != false) {
			return 90;
		}
		int i_20_ = i >>> 1;
		i_20_ |= i_20_ >>> 1;
		i_20_ |= i_20_ >>> 2;
		i_20_ |= i_20_ >>> 4;
		i_20_ |= i_20_ >>> 8;
		i_20_ |= i_20_ >>> 16;
		return i & (i_20_ ^ 0xffffffff);
	}

	final void method630(int i) {
		anObjectArray836 = null;
		anObjectArray786 = null;
		anObjectArray779 = null;
		anObjectArray788 = null;
		anObjectArray866 = null;
		anObjectArray748 = null;
		anObjectArray819 = null;
		anObjectArray806 = null;
		anObjectArray812 = null;
		anObjectArray735 = null;
		anObjectArray757 = null;
		anIntArray773 = null;
		anObjectArray790 = null;
		anObjectArray772 = null;
		anInt821++;
		anObjectArray793 = null;
		anObjectArray754 = null;
		anObjectArray887 = null;
		anObjectArray838 = null;
		anIntArray878 = null;
		anObjectArray839 = null;
		anObjectArray817 = null;
		anObjectArray744 = null;
		anObjectArray737 = null;
		anObjectArray727 = null;
		anObjectArray774 = null;
		anIntArray813 = null;
		anObjectArray719 = null;
		if (i != 4) {
			aBoolean893 = false;
		}
		anIntArray885 = null;
		anObjectArray789 = null;
		anObjectArray798 = null;
		anObjectArray747 = null;
		anObjectArray746 = null;
		anObjectArray835 = null;
		anObjectArray851 = null;
		anObjectArray763 = null;
		anIntArray726 = null;
		anObjectArray734 = null;
	}


	final void loadData(ByteBuffer buffer) throws IOException, BufferUnderflowException {
		anInt825++;
		if (buffer.limit() <= 0) {
			return;
		}
		int i_34_ = buffer.get();
		if (i_34_ == 255) {
			i_34_ = -1;
		}
		contentType = buffer.get();
		if ((0x80 & contentType) != 0) {
			contentType &= 0x7f;
			aString859 = ByteBufferUtils.getJagexString(buffer);
		}
		someInt = buffer.getShort();
		xPos = buffer.getShort();
		yPos = buffer.getShort();
		width = buffer.getShort();
		height = buffer.getShort();
		widthLayout = buffer.get();
		heightLayout = buffer.get();
		widthOffset = buffer.get();
		heightOffset = buffer.get();
		parentId = buffer.getShort();
		if (parentId != 65535) {
			parentId = parentId + (~0xffff & anInt721);
		} else {
			parentId = -1;
		}
		int i_35_ = buffer.get();
		aBoolean805 = (0x1 & i_35_) != 0;
		if (i_34_ >= 0) {
			aBoolean869 = (0x2 & i_35_) != 0;
		}
		if (contentType == 0) {
			anInt828 = buffer.getShort();
			anInt758 = buffer.getShort();
			if (i_34_ < 0) {
				aBoolean869 = buffer.get() == 1;
			}
		}
		if (contentType == 5) { //sprite
			spriteId = buffer.getInt();
			anInt824 = buffer.getShort();
			int i_36_ = buffer.get();
			aBoolean795 = (i_36_ & 0x2) != 0;
			aBoolean811 = (i_36_ & 0x1) != 0;
			anInt762 = buffer.get();
			anInt833 = buffer.get();
			anInt769 = buffer.getInt();
			aBoolean893 = buffer.get() == 1;
			aBoolean814 = buffer.get() == 1;
			anInt881 = buffer.getInt();
			if (i_34_ >= 3) {
				aBoolean799 = buffer.get() == 1;
			}
		}
		if (contentType == 6) {
			anInt741 = 1;
			anInt780 = buffer.getShort();
			if (anInt780 == 65535) {
				anInt780 = -1;
			}
			int i_37_ = buffer.get();
			aBoolean752 = (i_37_ & 0x4) == 4;
			boolean bool = (0x1 & i_37_) == 1;
			aBoolean850 = (i_37_ & 0x2) == 2;
			aBoolean783 = (i_37_ & 0x8) == 8;
			if (bool) {
				anInt848 = buffer.getShort();
				anInt730 = buffer.getShort();
				anInt871 = buffer.getShort();
				anInt760 = buffer.getShort();
				anInt797 = buffer.getShort();
				anInt767 = buffer.getShort();
			} else if (aBoolean850) {
				anInt848 = buffer.getShort();
				anInt730 = buffer.getShort();
				anInt749 = buffer.getShort();
				anInt871 = buffer.getShort();
				anInt760 = buffer.getShort();
				anInt797 = buffer.getShort();
				anInt767 = buffer.getShort();
			}
			anInt740 = buffer.getShort();
			if (anInt740 == 65535) {
				anInt740 = -1;
			}
			if (widthLayout != 0) {
				anInt761 = buffer.getShort();
			}
			if (heightLayout != 0) {
				anInt729 = buffer.getShort();
			}
		}
		if (contentType == 4) { //text
			fontId = buffer.getShort();
			if (fontId == 65535) {
				fontId = -1;
			}
			if (i_34_ >= 2) {
				aBoolean876 = buffer.get() == 1;
			}
			aString781 = ByteBufferUtils.getJagexString(buffer);
			anInt845 = buffer.get();
			anInt722 = buffer.get();
			anInt857 = buffer.get();
			aBoolean891 = buffer.get() == 1;
			anInt881 = buffer.getInt();
			anInt762 = buffer.get();
			if (i_34_ >= 0) {
				anInt755 = buffer.get();
			}
		}
		if (contentType == 3) { //rect
			anInt881 = buffer.getInt();
			filled = buffer.get() == 1;
			anInt762 = buffer.get();
		}
		if (contentType == 9) {
			anInt718 = buffer.get();
			anInt881 = buffer.getInt();
			aBoolean759 = buffer.get() == 1;
		}
		int i_38_ = ByteBufferUtils.getTriByte(buffer);
		int i_39_ = buffer.get();
		if (i_39_ != 0) {
			anIntArray822 = new int[11];
			aByteArray753 = new byte[11];
			aByteArray738 = new byte[11];
			for (/**/; i_39_ != 0; i_39_ = buffer.get()) {
				int i_40_ = -1 + (i_39_ >> 4);
				i_39_ = i_39_ << 8 | buffer.get();
				i_39_ &= 0xfff;
				if (i_39_ == 4095) {
					i_39_ = -1;
				}
				byte b = buffer.get();
				if (b != 0) {
					aBoolean724 = true;
				}
				byte b_41_ = buffer.get();
				anIntArray822[i_40_] = i_39_;
				aByteArray753[i_40_] = b;
				aByteArray738[i_40_] = b_41_;
			}
		}
		aString888 = ByteBufferUtils.getJagexString(buffer);
		int i_42_ = buffer.get();
		int i_43_ = i_42_ & 0xf;
		int i_44_ = i_42_ >> 4;
				if (i_43_ > 0) {
					aStringArray842 = new String[i_43_];
					for (int i_45_ = 0; i_45_ < i_43_; i_45_++)
						aStringArray842[i_45_] = ByteBufferUtils.getJagexString(buffer);
				}
				if (i_44_ > 0) {
					int i_46_ = buffer.get();
					anIntArray874 = new int[1 + i_46_];
					for (int i_47_ = 0; anIntArray874.length > i_47_; i_47_++)
						anIntArray874[i_47_] = -1;
					anIntArray874[i_46_] = buffer.getShort();
				}
				if (i_44_ > 1) {
					int i_48_ = buffer.get();
					anIntArray874[i_48_] = buffer.getShort();
				}
				aString830 = ByteBufferUtils.getJagexString(buffer);
				if (aString830.equals("")) {
					aString830 = null;
				}
				anInt832 = buffer.get();
				anInt770 = buffer.get();
				anInt882 = buffer.get();
				aString879 = ByteBufferUtils.getJagexString(buffer);
				int i_49_ = -1;
				if ((i_38_ & 0x3f966) >> 11 != 0) {
					i_49_ = buffer.getShort();
					anInt764 = buffer.getShort();
					if (i_49_ == 65535) {
						i_49_ = -1;
					}
					if (anInt764 == 65535) {
						anInt764 = -1;
					}
					anInt745 = buffer.getShort();
					if (anInt745 == 65535) {
						anInt745 = -1;
					}
				}
				if (i_34_ >= 0) {
					anInt870 = buffer.getShort();
					if (anInt870 == 65535) {
						anInt870 = -1;
					}
				}
				//aNode_Sub30_816 = new Node_Sub30(i_38_, i_49_);
				if (-1 >= (i_34_ ^ 0xffffffff)) {
					int i_50_ =buffer.get();
					for (int i_51_ = 0; i_50_ > i_51_; i_51_++) {
						int i_52_ = ByteBufferUtils.getTriByte(buffer);
						int i_53_ = buffer.getInt();
						//aClass262_792.method3096((long) i_52_, (byte) -37, new Node_Sub16(i_53_));
					}
					int i_54_ = buffer.get();
					for (int i_55_ = 0; i_54_ > i_55_; i_55_++) {
						int i_56_ = ByteBufferUtils.getTriByte(buffer);
						buffer.get();
						String string = ByteBufferUtils.getJagexString(buffer);
						//aClass262_792.method3096((long) i_56_, (byte) -37, new Node_Sub5(string));
					}
				}
				anObjectArray735 = method623((byte) -82, buffer);
				anObjectArray838 = method623((byte) -82, buffer);
				anObjectArray747 = method623((byte) -82, buffer);
				anObjectArray806 = method623((byte) -82, buffer);
				anObjectArray757 = method623((byte) -82, buffer);
				anObjectArray851 = method623((byte) -82, buffer);
				anObjectArray790 = method623((byte) -82, buffer);
				anObjectArray719 = method623((byte) -82, buffer);
				anObjectArray789 = method623((byte) -82, buffer);
				anObjectArray839 = method623((byte) -82, buffer);
				if (i_34_ >= 0) {
					anObjectArray737 = method623((byte) -82, buffer);
				}
				anObjectArray866 = method623((byte) -82, buffer);
				anObjectArray812 = method623((byte) -82, buffer);
				anObjectArray779 = method623((byte) -82, buffer);
				anObjectArray786 = method623((byte) -82, buffer);
				anObjectArray835 = method623((byte) -82, buffer);
				anObjectArray819 = method623((byte) -82, buffer);
				anObjectArray744 = method623((byte) -82, buffer);
				anObjectArray774 = method623((byte) -82, buffer);
				anObjectArray793 = method623((byte) -82, buffer);
				anObjectArray772 = method623((byte) -82, buffer);
				anIntArray726 = method622((byte) 112, buffer);
				anIntArray773 = method622((byte) 112, buffer);
				anIntArray885 = method622((byte) 112, buffer);
				anIntArray813 = method622((byte) 112, buffer);
				anIntArray878 = method622((byte) 112, buffer);
	}

	final void method638(short s, short s_57_, byte b, int i) {
		anInt751++;
		if (b != -34) {
			aBoolean811 = true;
		}
		if (i < 5) {
			if (aShortArray875 == null) {
				aShortArray875 = new short[5];
				aShortArray827 = new short[5];
			}
			aShortArray875[i] = s_57_;
			aShortArray827[i] = s;
		}
	}

	public RSInterface() {
		anInt721 = -1;
		height = 0;
		anInt740 = -1;
		anInt761 = 0;
		spriteId = -1;
		aString781 = "";
		anInt768 = 0;
		aBoolean783 = false;
		anInt776 = 0;
		anInt749 = 0;
		anInt755 = 0;
		anInt730 = 0;
		anInt729 = 0;
		aBoolean811 = false;
		anInt718 = 1;
		anInt809 = 0;
		anInt762 = 0;
		aBoolean771 = false;
		anInt758 = 0;
		aBoolean759 = false;
		aBoolean805 = false;
		xPos = 0;
		anInt794 = -1;
		anInt807 = -1;
		anInt722 = 0;
		anInt810 = -1;
		filled = false;
		aBoolean752 = false;
		anInt833 = 0;
		fontId = -1;
		anInt791 = -1;
		aBoolean799 = true;
		anInt845 = 0;
		anInt849 = -1;
		widthLayout = (byte) 0;
		anInt745 = -1;
		anInt767 = 100;
		anInt844 = 0;
		anInt778 = 1;
		anInt769 = 0;
		aClass51_864 = null;
		anInt756 = 0;
		aBoolean796 = false;
		anInt832 = 0;
		anInt733 = 2;
		anInt808 = -1;
		yPos = 0;
		aBoolean869 = false;
		anInt802 = 0;
		anInt862 = 0;
		heightOffset = (byte) 0;
		width = 0;
		anInt853 = 0;
		anInt834 = 0;
		anInt764 = -1;
		anInt828 = 0;
		anInt858 = -1;
		aBoolean876 = true;
		anInt770 = 0;
		anInt741 = 1;
		anInt857 = 0;
		aBoolean724 = false;
		anInt881 = 0;
		parentId = -1;
		anInt815 = 1;
		anInt848 = 0;
		anInt739 = 0;
		aString888 = "";
		anInt882 = 0;
		anInt829 = -1;
		aString879 = "";
		anInt824 = 0;
		anInt873 = 0;
		aBoolean872 = false;
		screenWidth = 0;
		aBoolean795 = false;
		aBoolean891 = false;
		anInt895 = 0;
		anInt884 = 0;
		aBoolean877 = false;
		widthOffset = (byte) 0;
		screenHeight = 0;
		anInt871 = 0;
		heightLayout = (byte) 0;
		anInt760 = 0;
		anInt797 = 0;
		anInt896 = -1;
		anInt870 = -1;
	}

	public static RSInterface get(Cache cache, int id) {
		if (!interfaces.containsKey(id)) {
			RSInterface parent = new RSInterface();
			try {
				for (int i = 0; i < cache.getContainerCount(3, id); i++) {
					RSInterface child = new RSInterface();
					child.loadData(cache.read(3, id, i));
					parent.children.add(child);
				}
			} catch (Exception e) {
				interfaces.put(id, parent);
			}
		}
		return interfaces.get(id);
	}

	public static String getChildTypeName(int contentType) {
		switch (contentType) {
		case 5:
			return "Sprite";
		case 4:
			return "Text";
		case 3:
			return "Rectangle";
		default:
			return "Unknown";
		}
	}

}
