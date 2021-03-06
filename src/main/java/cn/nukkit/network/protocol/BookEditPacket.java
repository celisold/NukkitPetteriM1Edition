package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class BookEditPacket extends DataPacket {

    public static final int TYPE_REPLACE_PAGE = 0;
    public static final int TYPE_ADD_PAGE = 1;
    public static final int TYPE_DELETE_PAGE = 2;
    public static final int TYPE_SWAP_PAGES = 3;
    public static final int TYPE_SIGN_BOOK = 4;

    public int type;
    public int inventorySlot;
    public int pageNumber;
    public int secondaryPageNumber;
    public String text;
    public String photoName;
    public String title;
    public String author;
    public String xuid;

    @Override
    public byte pid() {
        return ProtocolInfo.BOOK_EDIT_PACKET;
    }

    @Override
    public void decode() {
        this.type = this.getByte();
        this.inventorySlot = this.getByte();

        switch (this.type) {
            case TYPE_REPLACE_PAGE:
			case TYPE_ADD_PAGE:
                this.pageNumber = this.getByte();
                this.text = this.getString();
                this.photoName = this.getString();
                break;
            case TYPE_DELETE_PAGE:
                this.pageNumber = this.getByte();
                break;
            case TYPE_SWAP_PAGES:
                this.pageNumber = this.getByte();
                this.secondaryPageNumber = this.getByte();
                break;
            case TYPE_SIGN_BOOK:
                this.title = this.getString();
                this.author = this.getString();
                this.xuid = this.getString();
                break;
        }
    }

    @Override
    public void encode() {
    }
}
