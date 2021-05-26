import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class MyButton extends JButton {//Наследуемся от класса кнопки(Тоже что и JFrame)

    private boolean isLastButton;

    public MyButton() {

        super();

        initUI();
    }

    public MyButton(Image image) {

        super(new ImageIcon(image));

        initUI();
    }

    //когда наводим мышкой на кнопку, ее границы становятся желтыми
    private void initUI() {

        isLastButton = false;
        BorderFactory.createLineBorder(Color.gray);//Делаем обводку серого цвета

        addMouseListener(new MouseAdapter() {//

            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.yellow));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.gray));
            }
        });
    }

    public void setLastButton() {

        isLastButton = true;
    }

    //пустая кнопка без изображения
    public boolean isLastButton() {

        return isLastButton;
    }
}