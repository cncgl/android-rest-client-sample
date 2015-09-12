package cogel.jp.volleysample;

/**
 * Created by shigeru on 15/09/11.
 */
import java.util.ArrayList;
import java.util.List;

/**
 * Created by m_iwasaki on 15/03/12.
 */
public class Todo {
    private long id;
    private int colorLabel;
    private long createdTime;
    private String value;

    public static interface ColorLabel {
        public static final int NONE = 1;
        public static final int PINK = 2;
        public static final int INDIGO = 3;
        public static final int GREEN = 4;
        public static final int AMBER = 5;
    }

    // Constructor
    //--------------------------------------------------------
    public Todo(long id, int colorLabel, String value, long createdTime) {
        this.id = id;
        this.colorLabel = colorLabel;
        this.value = value;
        this.createdTime = createdTime;
    }

    // Accessor
    //--------------------------------------------------
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public int getColorLabel() {
        return colorLabel;
    }

    public void setColorLabel(int colorLabel) {
        this.colorLabel = colorLabel;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * テスト表示用にダミーのリストアイテムを作成.
     */
    public static List<Todo> addDummyItem() {
        List<Todo> items = new ArrayList<>();
        items.add(new Todo(1L, Todo.ColorLabel.INDIGO, "猫に小判", System.currentTimeMillis() + 1));
        items.add(new Todo(2L, Todo.ColorLabel.PINK, "猫の手も借りたい", System.currentTimeMillis() + 2));
        items.add(new Todo(3L, Todo.ColorLabel.GREEN, "窮鼠猫を噛む", System.currentTimeMillis() + 3));
        items.add(new Todo(4L, Todo.ColorLabel.AMBER,
                "猫は三年飼っても三日で恩を忘れる", System.currentTimeMillis() + 4));
        items.add(new Todo(5L, Todo.ColorLabel.NONE, "猫も杓子も", System.currentTimeMillis() + 5));
        return items;
    }
}
