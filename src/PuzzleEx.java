import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PuzzleEx extends JFrame {

    private JPanel panel;
    private BufferedImage source;
    private BufferedImage resized;
    private Image image;
    private MyButton lastButton;
    private int width, height;

    private java.util.List<MyButton> buttons;
    private java.util.List<Point> solution;

    private final int NUMBER_OF_BUTTONS = 12;
    private final int DESIRED_WIDTH = 300;//устанавливаем размер для желаемой ширины

    public PuzzleEx() {

        initUI();
    }

    //Сохраняем правильный порядок кнопок, образующих изображение.
    private void initUI() {

        solution = new ArrayList<>();

        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));

        buttons = new ArrayList<>();

        //GridLayout - хранит наши компоненты. Макет состоит из 4 строк и 3 столбцов.
        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 3, 0, 0));

        try {
            source = loadImage();
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, h,
                    BufferedImage.TYPE_INT_ARGB);

        } catch (IOException ex) {
            Logger.getLogger(PuzzleEx.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        width = resized.getWidth(null);
        height = resized.getHeight(null);

        add(panel, BorderLayout.CENTER);

        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 3; j++) {
                //Вырезает прямоугольные пормы из исходного изображения с уже измененным размером.
                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / 3, i * height / 4,
                                (width / 3), height / 4)));

                //Кнопка содержащая правильный ряд и положение столбца, кнопки на картинке.
                MyButton button = new MyButton(image);
                button.putClientProperty("position", new Point(i, j));

                //Пустая кнопка заходит в конец сетки в правом нижнем углу. Это кнопка которая меняет свое положение с нажатой соседней кнопкой. Мы устанавливаем его isLastButton флаг с помощью setLastButton()
                if (i == 3 && j == 2) {
                    lastButton = new MyButton();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton();
                    lastButton.putClientProperty("position", new Point(i, j));
                } else {
                    buttons.add(button);
                }
            }
        }
        //Задаем случайный порядок элементов button списка, получаем перемешанное изображение, пустая кнопка уходит в нижний правый угол
        Collections.shuffle(buttons);
        buttons.add(lastButton);

        //Размещаем элементы на панели и создаем серую рамку вокруг кнопок + добавляем обработчит действий клика
        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {

            MyButton btn = buttons.get(i);
            panel.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(new ClickAction());
        }

        pack();
        setTitle("Puzzle");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //Вычисляем новую высоту, сохраняя пропорции изображения
    private int getNewHeight(int w, int h) {

        double ratio = DESIRED_WIDTH / (double) w;
        int newHeight = (int) (h * ratio);
        return newHeight;
    }

    //Добавляем и обрабатываем изображение
    private BufferedImage loadImage() throws IOException {

        BufferedImage bimg = ImageIO.read(new File("Obito.jpg"));

        return bimg;
    }

    //Меняем размер исходного изображения путем создания нового буфферИмейдж с новым размером, затем отрисовываем буферизированное изображение
    private BufferedImage resizeImage(BufferedImage originalImage, int width,
                                      int height, int type) throws IOException {

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    private class ClickAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

            checkButton(e);
            checkSolution();
        }
        //Кнопки со списка сопоставляются с сеткой панели. Получаем индексы последней кнопки и нажатой кнопки. Они меняются местами с помощью Collections.swap()сли они смежные
        private void checkButton(ActionEvent e) {

            int lidx = 0;

            for (MyButton button : buttons) {
                if (button.isLastButton()) {
                    lidx = buttons.indexOf(button);
                }
            }

            JButton button = (JButton) e.getSource();
            int bidx = buttons.indexOf(button);

            if ((bidx - 1 == lidx) || (bidx + 1 == lidx)
                    || (bidx - 3 == lidx) || (bidx + 3 == lidx)) {
                Collections.swap(buttons, bidx, lidx);
                updateButtons();
            }
        }
        //updateButtons()Метод отображает список в сетке панели.
        // Сначала с помощью removeAll() метода удаляются все компоненты .
        // Цикл for используется для просмотра buttonsсписка и добавления
        // переупорядоченных кнопок обратно в диспетчер компоновки панели.
        // Наконец, validate()метод реализует новый макет.
        private void updateButtons() {

            panel.removeAll();

            for (JComponent btn : buttons) {

                panel.add(btn);
            }

            panel.validate();
        }
    }
//Проверка решения выполняется путем
// сравнения списка точек правильно упорядоченных кнопок
// с текущим списком, содержащим порядок кнопок из окна.
// В случае достижения решения отображается диалоговое окно с сообщением.
    private void checkSolution() {

        java.util.List<Point> current = new ArrayList<>();

        for (JComponent btn : buttons) {
            current.add((Point) btn.getClientProperty("position"));
        }

        if (compareList(solution, current)) {
            JOptionPane.showMessageDialog(panel, "Finished",
                    "Congratulation", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static boolean compareList(List<Point> ls1, List<Point> ls2) {

        return ls1.toString().contentEquals(ls2.toString());
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                PuzzleEx puzzle = new PuzzleEx();
                puzzle.setVisible(true);
            }
        });
    }
}