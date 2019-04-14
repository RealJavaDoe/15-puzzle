import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Jean-Pierre PEIFFER
 * @edition 2019
 * @version 2.01
 * 
 *          This code works fine with Windows 10 and Java 10
 * 
 */

@SuppressWarnings({ "unused", "serial" })
public class FifteenPuzzle extends JFrame {

	private static final Color COLOR_1 = new Color(245, 245, 245);
	private static final Color COLOR_2 = new Color(220, 220, 220);
	private static final Color COLOR_3 = new Color(255, 230, 230);
	private static final Color COLOR_4 = new Color(240, 240, 240);
	private static final Border BORDER_1 = BorderFactory.createMatteBorder(2, 2, 2, 2, COLOR_1);
	private static final Border BORDER_2 = BorderFactory.createLoweredBevelBorder();
	private static final Dimension DIM_1 = new Dimension(120, 512);
	private static final Dimension DIM_2 = new Dimension(102, 102);
	private static final Dimension DIM_3 = new Dimension(512, 512);
	private static final Dimension DIM_4 = new Dimension(40, 512);
	private static final Dimension DIM_5 = new Dimension(120, 45);
	private static final Dimension DIM_6 = new Dimension(40, 40);
	private static final Dimension DIM_7 = new Dimension(512, 45);
	private static final Dimension DIM_8 = new Dimension(60, 40);
	private static final Dimension DIM_9 = new Dimension(40, 45);
	private static final Dimension DIM_10 = new Dimension(384, 45);
	private static final Dimension DIM_11 = new Dimension(128, 45);
	private static final Dimension DIM_12 = new Dimension(1240, 620);
	private static final Font FONT_1 = new Font("Arial", Font.BOLD, 18);
	private static final Font FONT_2 = new Font("Arial", Font.BOLD, 32);
	private static final Font FONT_3 = new Font("Arial", Font.BOLD, 16);
	private static final int ROW_HEIGHT = 25;
	private static final String NAME = "15-puzzle | version 2.01";
	private static final String[] FILE = { "C:/15-puzzle/data/scores.xls", "C:/15-puzzle/images/stock1",
			"C:/15-puzzle/images/stock1/image", "C:/15-puzzle/images/stock2", "C:/15-puzzle/images/stock2/image",
			"C:/15-puzzle/lan/all.json" };
	private static final String[] AUDIO_FILE = { "C:/15-puzzle/audio/random.wav", "C:/15-puzzle/audio/list.wav",
			"C:/15-puzzle/audio/color.wav", "C:/15-puzzle/audio/highscores.wav", "C:/15-puzzle/audio/sound.wav",
			"C:/15-puzzle/audio/scramble.wav", "C:/15-puzzle/audio/thumbnail.wav", "C:/15-puzzle/audio/blocked.wav",
			"C:/15-puzzle/audio/move.wav", "C:/15-puzzle/audio/end.wav", "C:/15-puzzle/audio/language.wav",
			"C:/15-puzzle/audio/numberDisplay.wav" };
	private static final String[] IMAGE_FILE = { "C:/15-puzzle/images/buttons/random.png",
			"C:/15-puzzle/images/buttons/list.png", "C:/15-puzzle/images/buttons/color.png",
			"C:/15-puzzle/images/buttons/soundOn.png", "C:/15-puzzle/images/buttons/soundOff.png",
			"C:/15-puzzle/images/buttons/scramble.png", "C:/15-puzzle/images/buttons/numberDisplay.png" };
	private static final String[] LANGUAGE = { "EN", "FR", "ES", "IT", "DE" };
	private Box[] box = new Box[4];
	private Image croppedImage;
	private Image originalImage;
	private Image[] thumbnailImage = new Image[5];
	private ImageIcon[] thumbnailIcon = new ImageIcon[5];
	private JButton tileButton;
	private JButton[] optionButton = new JButton[14];
	private JLabel emptyTileLabel;
	private JLabel imageLabel;
	private static JPanel container = new JPanel();
	private JPanel[] panel = new JPanel[9];
	private String[] text = new String[22];
	int index;
	int totalNumberOfImages;
	int numberOfTheSelectedImage;
	int[] thumbnailNumbers;
	int numberOfMoves = 0;
	int[][] position = new int[][] { { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 14, 15 } };
	int[] newOrder = new int[16];
	boolean soundOn;
	boolean color;
	boolean numberDisplay;
	int language;
	int pictureFolder = 2;
	private static final int NUMBER_OF_SCORES_TO_BE_DISPLAYED = 10;
	private File scoresFile = new File(FILE[0]);
	private HSSFWorkbook workbook = new HSSFWorkbook();
	private HSSFSheet sheet1 = workbook.createSheet();
	private HSSFSheet sheet2 = workbook.createSheet();
	private FileOutputStream file;
	private FileInputStream fileInputStream;
	private FileOutputStream fileOutputStream;
	private HSSFRow row;
	private Cell cell;
	private CellReference cellReference;

	@SuppressWarnings("static-access")
	public FifteenPuzzle(JPanel container, int[] thumbnailNumbers, int totalNumberOfImages, int index,
			int numberOfTheSelectedImage, boolean soundOn, boolean color, int language, boolean numberDisplay) {
		this.container = container;
		this.thumbnailNumbers = thumbnailNumbers;
		this.totalNumberOfImages = totalNumberOfImages;
		this.index = index;
		this.numberOfTheSelectedImage = numberOfTheSelectedImage;
		this.soundOn = soundOn;
		this.color = color;
		this.language = language;
		this.numberDisplay = numberDisplay;
		createWorkbook();
		getText(this.language);
	}

	public void createWorkbook() {
		if (!scoresFile.exists()) {
			try {
				file = new FileOutputStream(FILE[0]);
				workbook.write(file);
				file.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static int getNumberOfImages(File file) {
		if (!file.exists())
			try {
				throw new FileNotFoundException();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return file.list().length;
	}

	public static int[] getRandomNumbers(int number) {
		if (number == 16)
			number--;
		int[] randomNumbers = new int[number];
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		for (int i = 0; i < randomNumbers.length; i++) {
			arrayList.add(i);
		}
		Random random = new Random();
		for (int i = 0; i < randomNumbers.length; i++) {
			int index = random.nextInt(arrayList.size());
			randomNumbers[i] = arrayList.get(index);
			arrayList.remove(index);
		}
		return randomNumbers;
	}

	public ImageIcon getThumbnails(int i) {
		if (color)
			pictureFolder = 4;
		thumbnailIcon[i] = new ImageIcon(FILE[pictureFolder] + thumbnailNumbers[i] + ".png");
		thumbnailImage[i] = thumbnailIcon[i].getImage();
		thumbnailImage[i] = thumbnailImage[i].getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		return thumbnailIcon[i] = new ImageIcon(thumbnailImage[i]);
	}

	public int[] getSolubleGame() {
		boolean test = false;
		int[] randomNumbers = getRandomNumbers(16);
		while (test == false) {
			int counter = 0;
			for (int i = 0; i < 15; i++) {
				for (int j = 0; j < i; j++) {
					if (randomNumbers[j] > randomNumbers[i])
						counter++;
				}
			}
			if (counter % 2 == 0)
				test = true;
			else
				randomNumbers = getRandomNumbers(16);
		}
		return randomNumbers;
	}

	public void display() {
		if (color)
			pictureFolder = 4;
		container.setBackground(COLOR_1);
		UIManager.getDefaults().put("ToolTip.background", COLOR_1);
		panel[0] = new JPanel();
		panel[0].setPreferredSize(DIM_1);
		panel[0].setBackground(COLOR_1);
		box[0] = Box.createVerticalBox();
		for (int i = 0; i < 5; i++) {
			optionButton[i] = new JButton(getThumbnails(i));
			optionButton[i].setPreferredSize(DIM_2);
			optionButton[i].setToolTipText(text[0] + " " + (thumbnailNumbers[i] + 1));
			if (i == (index - 1))
				optionButton[i].setEnabled(false);
			box[0].add(optionButton[i]);
		}
		optionButton[0].addActionListener(new Thumbnail1Listener());
		optionButton[1].addActionListener(new Thumbnail2Listener());
		optionButton[2].addActionListener(new Thumbnail3Listener());
		optionButton[3].addActionListener(new Thumbnail4Listener());
		optionButton[4].addActionListener(new Thumbnail5Listener());
		panel[0].add(box[0]);
		panel[1] = new JPanel();
		panel[1].setPreferredSize(DIM_3);
		panel[1].setBackground(COLOR_1);
		imageLabel = new JLabel(new ImageIcon(FILE[pictureFolder] + numberOfTheSelectedImage + ".png"));
		panel[1].add(imageLabel);
		panel[2] = new JPanel();
		panel[2].setPreferredSize(DIM_4);
		panel[2].setBackground(COLOR_1);
		panel[3] = new JPanel();
		panel[3].setPreferredSize(DIM_3);
		panel[3].setBackground(Color.WHITE);
		panel[3].setBorder(BORDER_1);
		panel[3].setLayout(new GridLayout(4, 4, 0, 0));
		panel[4] = new JPanel();
		panel[4].setPreferredSize(DIM_5);
		panel[4].setBackground(COLOR_1);
		optionButton[5] = new JButton(new ImageIcon(IMAGE_FILE[0]));
		optionButton[5].setToolTipText(text[1]);
		optionButton[5].setPreferredSize(DIM_6);
		optionButton[5].setBackground(COLOR_2);
		optionButton[5].setUI(new ButtonDesign());
		optionButton[5].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] images = getRandomNumbers(totalNumberOfImages);
				new FifteenPuzzle(container, images, totalNumberOfImages, 1, images[0], soundOn, color, language,
						numberDisplay).display();
				if (soundOn)
					playSound(AUDIO_FILE[0]);
			}
		});
		panel[4].add(optionButton[5]);
		optionButton[6] = new JButton(new ImageIcon(IMAGE_FILE[1]));
		optionButton[6].setToolTipText(text[2]);
		optionButton[6].setPreferredSize(DIM_6);
		optionButton[6].setBackground(COLOR_2);
		optionButton[6].setUI(new ButtonDesign());
		optionButton[6].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectPictures();
				if (soundOn)
					playSound(AUDIO_FILE[1]);
			}
		});
		panel[4].add(optionButton[6]);
		panel[5] = new JPanel();
		panel[5].setPreferredSize(DIM_7);
		panel[5].setBackground(COLOR_1);
		optionButton[7] = new JButton(new ImageIcon(IMAGE_FILE[2]));
		optionButton[7].setToolTipText(text[3]);
		optionButton[7].setPreferredSize(DIM_6);
		optionButton[7].setBackground(COLOR_2);
		optionButton[7].setUI(new ButtonDesign());
		optionButton[7].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (color)
					color = false;
				else
					color = true;
				if (soundOn)
					playSound(AUDIO_FILE[2]);
				int[] images = getRandomNumbers(totalNumberOfImages);
				new FifteenPuzzle(container, images, totalNumberOfImages, 1, images[0], soundOn, color, language,
						numberDisplay).display();
			}
		});
		panel[5].add(optionButton[7]);
		optionButton[8] = new JButton("" + (numberOfTheSelectedImage + 1));
		optionButton[8].setToolTipText(text[4]);
		optionButton[8].setPreferredSize(DIM_8);
		optionButton[8].setBackground(COLOR_2);
		optionButton[8].setFont(FONT_1);
		optionButton[8].setUI(new ButtonDesign());
		optionButton[8].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String[][] imageScores = getImageScores(numberOfTheSelectedImage + 1);
				if (soundOn)
					playSound(AUDIO_FILE[3]);
				if (imageScores.length == 0 || imageScores[0][0] == null) {
					JOptionPane.showMessageDialog(null, text[15], text[19], JOptionPane.INFORMATION_MESSAGE);
					return;
				} else
					displayImageHighscores(imageScores);
			}
		});
		panel[5].add(optionButton[8]);
		panel[6] = new JPanel();
		panel[6].setPreferredSize(DIM_9);
		panel[6].setBackground(COLOR_1);
		panel[7] = new JPanel();
		panel[7].setPreferredSize(DIM_10);
		panel[7].setBackground(COLOR_1);
		optionButton[9] = new JButton(new ImageIcon(IMAGE_FILE[3]));
		optionButton[9].setToolTipText(text[5]);
		optionButton[9].setPreferredSize(DIM_6);
		optionButton[9].setBackground(COLOR_2);
		optionButton[9].setUI(new ButtonDesign());
		optionButton[9].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				soundOn = false;
				panel[7].removeAll();
				panel[7].add(optionButton[10]);
				panel[7].add(optionButton[11]);
				panel[7].add(optionButton[12]);
				panel[7].add(optionButton[13]);
				panel[7].validate();
			}
		});
		optionButton[10] = new JButton(new ImageIcon(IMAGE_FILE[4]));
		optionButton[10].setToolTipText(text[6]);
		optionButton[10].setPreferredSize(DIM_6);
		optionButton[10].setBackground(COLOR_2);
		optionButton[10].setUI(new ButtonDesign());
		optionButton[10].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				soundOn = true;
				playSound(AUDIO_FILE[4]);
				panel[7].removeAll();
				panel[7].add(optionButton[9]);
				panel[7].add(optionButton[11]);
				panel[7].add(optionButton[12]);
				panel[7].add(optionButton[13]);
				panel[7].validate();
			}
		});
		optionButton[11] = new JButton(new ImageIcon(IMAGE_FILE[5]));
		optionButton[11].setToolTipText(text[7]);
		optionButton[11].setPreferredSize(DIM_6);
		optionButton[11].setBackground(COLOR_2);
		optionButton[11].setUI(new ButtonDesign());
		optionButton[11].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (soundOn)
					playSound(AUDIO_FILE[5]);
				new FifteenPuzzle(container, thumbnailNumbers, totalNumberOfImages, index, thumbnailNumbers[index - 1],
						soundOn, color, language, numberDisplay).display();
			}
		});
		if (soundOn)
			panel[7].add(optionButton[9]);
		else
			panel[7].add(optionButton[10]);
		panel[7].add(optionButton[11]);
		optionButton[12] = new JButton(LANGUAGE[language]);
		optionButton[12].setToolTipText(text[8]);
		optionButton[12].setPreferredSize(DIM_8);
		optionButton[12].setBackground(COLOR_2);
		optionButton[12].setFont(FONT_1);
		optionButton[12].setUI(new ButtonDesign());
		optionButton[12].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (soundOn)
					playSound(AUDIO_FILE[10]);
				setLanguage();
			}
		});
		panel[7].add(optionButton[12]);
		optionButton[13] = new JButton(new ImageIcon(IMAGE_FILE[6]));
		optionButton[13].setToolTipText(text[9]);
		optionButton[13].setPreferredSize(DIM_6);
		optionButton[13].setBackground(COLOR_2);
		optionButton[13].setFont(FONT_1);
		optionButton[13].setUI(new ButtonDesign());
		optionButton[13].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (soundOn)
					playSound(AUDIO_FILE[11]);
				if (numberDisplay)
					numberDisplay = false;
				else
					numberDisplay = true;
				panel[3].removeAll();
				box[1].add(getNumberedTiles());
				container.validate();
			}
		});
		panel[7].add(optionButton[13]);
		panel[8] = new JPanel();
		panel[8].setPreferredSize(DIM_11);
		panel[8].setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel[8].setBackground(COLOR_3);
		JLabel movesLabel = new JLabel();
		movesLabel.setFont(FONT_2);
		movesLabel.setText("" + numberOfMoves);
		panel[8].add(movesLabel);
		panel[8].setBorder(BORDER_2);
		box[1] = Box.createHorizontalBox();
		box[1].add(panel[0]);
		box[1].add(panel[1]);
		box[1].add(panel[2]);
		box[1].add(getScrambledImage());
		box[2] = Box.createHorizontalBox();
		box[2].add(panel[4]);
		box[2].add(panel[5]);
		box[2].add(panel[6]);
		box[2].add(panel[7]);
		box[2].add(panel[8]);
		box[3] = Box.createVerticalBox();
		box[3].add(box[1]);
		box[3].add(box[2]);
		container.removeAll();
		container.add(box[3]);
		container.validate();
	}

	public JPanel getScrambledImage() {
		int[] randomNumbers = getSolubleGame();
		for (int i = 0; i < 15; i++) {
			newOrder[i] = randomNumbers[i];
		}
		newOrder[15] = 15;
		ImageIcon imageIcon = new ImageIcon(FILE[pictureFolder] + numberOfTheSelectedImage + ".png");
		originalImage = imageIcon.getImage();
		int row, column;
		for (int i = 0; i < 16; i++) {
			if (i == 15) {
				emptyTileLabel = new JLabel("");
				panel[3].add(emptyTileLabel);
			} else {
				row = getRow(newOrder[i]);
				column = getColumn(newOrder[i]);
				croppedImage = createImage(new FilteredImageSource(originalImage.getSource(),
						new CropImageFilter(column * 128, row * 128, 128, 128)));
				tileButton = new JButton();
				tileButton.addActionListener(new TileListener());
				tileButton.setIcon(new ImageIcon(croppedImage));
				tileButton.setBorderPainted(false);
				if (numberDisplay)
					tileButton.setUI(new NumberDisplay(getTileNumber(row, column)));
				panel[3].add(tileButton);
			}
		}
		return panel[3];
	}

	public int getRow(int newOrder) {
		int position;
		switch (newOrder) {
		case 4:
		case 5:
		case 6:
		case 7:
			position = 1;
			break;
		case 8:
		case 9:
		case 10:
		case 11:
			position = 2;
			break;
		case 12:
		case 13:
		case 14:
		case 15:
			position = 3;
			break;
		default:
			position = 0;
			break;
		}
		return position;
	}

	public int getColumn(int newOrder) {
		int position = 0;
		switch (newOrder) {
		case 1:
		case 5:
		case 9:
		case 13:
			position = 1;
			break;
		case 2:
		case 6:
		case 10:
		case 14:
			position = 2;
			break;
		case 3:
		case 7:
		case 11:
		case 15:
			position = 3;
			break;
		default:
			position = 0;
			break;
		}
		return position;
	}

	public int getTileNumber(int row, int column) {
		int number = 4 * row + column + 1;
		return number;
	}

	public JPanel getNumberedTiles() {
		int row, column;
		for (int i = 0; i < 16; i++) {
			if (newOrder[i] == 15) {
				emptyTileLabel = new JLabel("");
				panel[3].add(emptyTileLabel);
			} else {
				row = getRow(newOrder[i]);
				column = getColumn(newOrder[i]);
				croppedImage = createImage(new FilteredImageSource(originalImage.getSource(),
						new CropImageFilter(column * 128, row * 128, 128, 128)));
				tileButton = new JButton();
				tileButton.addActionListener(new TileListener());
				tileButton.setIcon(new ImageIcon(croppedImage));
				tileButton.setBorderPainted(false);
				if (numberDisplay)
					tileButton.setUI(new NumberDisplay(getTileNumber(row, column)));
				panel[3].add(tileButton);
			}
		}
		return panel[3];
	}

	public void setLanguage() {
		if (language != (LANGUAGE.length - 1))
			language++;
		else
			language = 0;
		getText(language);
		for (int i = 0; i < 5; i++) {
			optionButton[i].setToolTipText(text[0] + " " + (thumbnailNumbers[i] + 1));
		}
		for (int i = 5; i < 14; i++) {
			optionButton[i].setToolTipText(text[i - 4]);
		}
		optionButton[12].setText(LANGUAGE[language]);
	}

	@SuppressWarnings("unchecked")
	public String[] getText(int indexLang) {
		int i = 0;
		JSONParser parser = new JSONParser();
		try {
			Object object = parser.parse(new FileReader(FILE[5]));
			JSONObject jsonObject = (JSONObject) object;
			JSONArray value = (JSONArray) jsonObject.get(LANGUAGE[indexLang]);
			Iterator<String> iterator = value.iterator();
			while (iterator.hasNext()) {
				text[i] = iterator.next();
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return text;
	}

	public void switchNumbers(int indexStartTile, int indexFinishTile) {
		int temp;
		temp = newOrder[indexStartTile];
		newOrder[indexStartTile] = newOrder[indexFinishTile];
		newOrder[indexFinishTile] = temp;
	}

	public boolean isOver() {
		int counter = 0;
		boolean test = false;
		for (int i = 0; i < 15; i++) {
			if (newOrder[i] < newOrder[i + 1])
				counter++;
		}
		if (counter == 15)
			test = true;
		return test;
	}

	public void selectPictures() {
		int[] newThumbnailNumbers = new int[5];
		JFrame frame = new JFrame(text[10]);
		frame.setBackground(COLOR_1);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(0, 0, 320, 120);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		JPanel panel1 = new JPanel();
		panel1.setBackground(COLOR_1);
		JPanel panel2 = new JPanel();
		panel2.setBackground(COLOR_1);
		JComboBox<Integer> comboBox1 = new JComboBox<Integer>();
		JComboBox<Integer> comboBox2 = new JComboBox<Integer>();
		JComboBox<Integer> comboBox3 = new JComboBox<Integer>();
		JComboBox<Integer> comboBox4 = new JComboBox<Integer>();
		JComboBox<Integer> comboBox5 = new JComboBox<Integer>();
		for (int j = 0; j < totalNumberOfImages; j++) {
			comboBox1.addItem(j + 1);
			comboBox2.addItem(j + 1);
			comboBox3.addItem(j + 1);
			comboBox4.addItem(j + 1);
			comboBox5.addItem(j + 1);
		}
		comboBox1.setSelectedIndex(thumbnailNumbers[0]);
		panel2.add(comboBox1);
		comboBox2.setSelectedIndex(thumbnailNumbers[1]);
		panel2.add(comboBox2);
		comboBox3.setSelectedIndex(thumbnailNumbers[2]);
		panel2.add(comboBox3);
		comboBox4.setSelectedIndex(thumbnailNumbers[3]);
		panel2.add(comboBox4);
		comboBox5.setSelectedIndex(thumbnailNumbers[4]);
		panel2.add(comboBox5);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(COLOR_1);
		JButton confirmButton = new JButton(text[11]);
		confirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] img = new int[5];
				newThumbnailNumbers[0] = (int) comboBox1.getSelectedItem() - 1;
				newThumbnailNumbers[1] = (int) comboBox2.getSelectedItem() - 1;
				newThumbnailNumbers[2] = (int) comboBox3.getSelectedItem() - 1;
				newThumbnailNumbers[3] = (int) comboBox4.getSelectedItem() - 1;
				newThumbnailNumbers[4] = (int) comboBox5.getSelectedItem() - 1;
				for (int i = 0; i < 5; i++) {
					img[i] = newThumbnailNumbers[i];
				}
				Arrays.sort(img);
				if (img[0] < img[1] && img[1] < img[2] && img[2] < img[3] && img[3] < img[4]) {
					new FifteenPuzzle(container, newThumbnailNumbers, totalNumberOfImages, 1, newThumbnailNumbers[0],
							soundOn, color, language, numberDisplay).display();
					frame.dispose();
				} else {
					JOptionPane.showMessageDialog(null, text[16], text[21], JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
		});
		buttonsPanel.add(confirmButton);
		JButton cancelButton = new JButton(text[12]);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		buttonsPanel.add(cancelButton);
		panel1.add(panel2);
		panel1.add(buttonsPanel);
		frame.add(panel1);
		frame.setVisible(true);
	}

	public void saveAScore(int numberOfTheSelectedImage, int numberOfMoves) {
		HSSFSheet sheet;
		int sheetIndex = 0;
		if (color)
			sheetIndex = 1;
		String playerName = JOptionPane.showInputDialog(null, text[17], text[22], JOptionPane.QUESTION_MESSAGE);
		if (playerName == null)
			return;
		if (playerName.length() == 0)
			playerName = text[13];
		if (playerName.length() > 17)
			playerName = playerName.substring(0, 17);
		String[] data = new String[3];
		data[0] = playerName;
		data[1] = String.valueOf(numberOfTheSelectedImage + 1);
		data[2] = String.valueOf(numberOfMoves);
		try {
			fileInputStream = new FileInputStream(FILE[0]);
			workbook = new HSSFWorkbook(fileInputStream);
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, text[18], text[21], JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sheet = workbook.getSheetAt(sheetIndex);
		row = sheet.createRow(sheet.getPhysicalNumberOfRows());
		for (int i = 0; i < data.length; i++) {
			row.createCell(i).setCellValue(new HSSFRichTextString(data[i]));
		}
		try {
			fileOutputStream = new FileOutputStream(FILE[0]);
			workbook.write(fileOutputStream);
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, text[18], text[21], JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new FifteenPuzzle(container, thumbnailNumbers, totalNumberOfImages, index, thumbnailNumbers[index - 1], soundOn,
				color, language, numberDisplay).display();
	}

	public String[][] getImageScores(int numberOfTheSelectedImage) {
		HSSFSheet sheet;
		int sheetIndex = 0;
		if (color)
			sheetIndex = 1;
		String[][] allScores = null;
		int numberOfRows = 0;
		int counter = 0;
		try {
			fileInputStream = new FileInputStream(FILE[0]);
			workbook = new HSSFWorkbook(fileInputStream);
			sheet = workbook.getSheetAt(sheetIndex);
			numberOfRows = sheet.getPhysicalNumberOfRows();
			allScores = new String[numberOfRows][3];
			for (int i = 0; i < numberOfRows; i++) {
				cellReference = new CellReference("A" + String.valueOf(i + 1));
				row = sheet.getRow(cellReference.getRow());
				cell = row.getCell(cellReference.getCol());
				allScores[i][0] = cell.getStringCellValue();
				cellReference = new CellReference("B" + String.valueOf(i + 1));
				row = sheet.getRow(cellReference.getRow());
				cell = row.getCell(cellReference.getCol());
				allScores[i][1] = cell.getStringCellValue();
				cellReference = new CellReference("C" + String.valueOf(i + 1));
				row = sheet.getRow(cellReference.getRow());
				cell = row.getCell(cellReference.getCol());
				allScores[i][2] = cell.getStringCellValue();
			}
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, text[18], text[21], JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[][] temp = new String[numberOfRows][3];
		for (int i = 1; i < numberOfRows; i++) {
			for (int j = i; j > 0; j--) {
				if (Integer.valueOf(allScores[j][2]) < Integer.valueOf(allScores[j - 1][2])) {
					for (int k = 0; k < 3; k++) {
						temp[j][k] = allScores[j][k];
						allScores[j][k] = allScores[j - 1][k];
						allScores[j - 1][k] = temp[j][k];
					}
				}
			}
		}
		String[][] imageScores = new String[numberOfRows][3];
		for (int i = 0; i < numberOfRows; i++) {
			if (allScores[i][1].equals(String.valueOf(numberOfTheSelectedImage))) {
				imageScores[counter][0] = allScores[i][0];
				imageScores[counter][2] = allScores[i][2];
				counter++;
			}
		}
		return imageScores;
	}

	public void displayImageHighscores(String[][] imageScores) {
		JFrame frame = new JFrame(text[14] + (numberOfTheSelectedImage + 1) + ")");
		frame.setBackground(COLOR_1);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(0, 0, 330, 55 + NUMBER_OF_SCORES_TO_BE_DISPLAYED * 25);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		String[] header = { "", "", "" };
		Object[][] data = new Object[NUMBER_OF_SCORES_TO_BE_DISPLAYED][3];
		if (imageScores.length >= NUMBER_OF_SCORES_TO_BE_DISPLAYED) {
			for (int i = 0; i < NUMBER_OF_SCORES_TO_BE_DISPLAYED; i++) {
				data[i][0] = String.valueOf(i + 1);
				data[i][1] = imageScores[i][0];
				data[i][2] = imageScores[i][2];
			}
		} else {
			for (int i = 0; i < imageScores.length; i++) {
				data[i][0] = String.valueOf(i + 1);
				data[i][1] = imageScores[i][0];
				data[i][2] = imageScores[i][2];
			}
			for (int i = imageScores.length; i < NUMBER_OF_SCORES_TO_BE_DISPLAYED; i++) {
				data[i][0] = String.valueOf(i + 1);
				data[i][1] = null;
				data[i][2] = null;
			}
		}
		JTable table = new JTable(data, header) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setRowHeight(ROW_HEIGHT);
		table.setPreferredSize(new Dimension(270, table.getRowHeight() * table.getRowCount()));
		table.setCellSelectionEnabled(false);
		UIManager.put("Table.alternateRowColor", COLOR_4);
		table.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Table.gridColor")));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumn column1 = table.getColumnModel().getColumn(0);
		column1.setPreferredWidth(25);
		TableColumn column2 = table.getColumnModel().getColumn(1);
		column2.setPreferredWidth(190);
		DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
		tableCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(tableCellRenderer);
		}
		JPanel panel = new JPanel();
		panel.add(table);
		frame.add(panel);
		frame.setVisible(true);
	}

	public void playSound(String sound) {
		Clip clip = null;
		File file = new File(sound);
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		clip.start();
	}

	class Thumbnail1Listener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			new FifteenPuzzle(container, thumbnailNumbers, totalNumberOfImages, 1, thumbnailNumbers[0], soundOn, color,
					language, numberDisplay).display();
			if (soundOn)
				playSound(AUDIO_FILE[6]);
		};

	}

	class Thumbnail2Listener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			new FifteenPuzzle(container, thumbnailNumbers, totalNumberOfImages, 2, thumbnailNumbers[1], soundOn, color,
					language, numberDisplay).display();
			if (soundOn)
				playSound(AUDIO_FILE[6]);
		};

	}

	class Thumbnail3Listener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			new FifteenPuzzle(container, thumbnailNumbers, totalNumberOfImages, 3, thumbnailNumbers[2], soundOn, color,
					language, numberDisplay).display();
			if (soundOn)
				playSound(AUDIO_FILE[6]);
		};

	}

	class Thumbnail4Listener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			new FifteenPuzzle(container, thumbnailNumbers, totalNumberOfImages, 4, thumbnailNumbers[3], soundOn, color,
					language, numberDisplay).display();
			if (soundOn)
				playSound(AUDIO_FILE[6]);
		};

	}

	class Thumbnail5Listener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			new FifteenPuzzle(container, thumbnailNumbers, totalNumberOfImages, 5, thumbnailNumbers[4], soundOn, color,
					language, numberDisplay).display();
			if (soundOn)
				playSound(AUDIO_FILE[6]);
		};

	}

	class TileListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			JButton startTileButton = (JButton) e.getSource();
			Dimension dimStartTile = startTileButton.getSize();
			int startTileX = startTileButton.getX();
			int startTileY = startTileButton.getY();
			int finishTileX = emptyTileLabel.getX();
			int finishTileY = emptyTileLabel.getY();
			int indexStartTile = position[startTileY / dimStartTile.height][startTileX / dimStartTile.width];
			int indexFinishTile;
			if (finishTileX == startTileX && finishTileY == startTileY + dimStartTile.height) {
				indexFinishTile = indexStartTile + 4;
			} else if (finishTileX == startTileX && finishTileY == startTileY - dimStartTile.height) {
				indexFinishTile = indexStartTile - 4;
			} else if (finishTileX == startTileX + dimStartTile.width && finishTileY == startTileY) {
				indexFinishTile = indexStartTile + 1;
			} else if (finishTileX == startTileX - dimStartTile.width && finishTileY == startTileY) {
				indexFinishTile = indexStartTile - 1;
			} else {
				if (soundOn)
					playSound(AUDIO_FILE[7]);
				return;
			}
			numberOfMoves++;
			if (soundOn)
				playSound(AUDIO_FILE[8]);
			panel[3].add(emptyTileLabel, indexStartTile);
			panel[3].add(startTileButton, indexFinishTile);
			panel[3].validate();
			panel[8].removeAll();
			JLabel movesLabel = new JLabel();
			movesLabel.setFont(FONT_2);
			movesLabel.setText("" + numberOfMoves);
			panel[8].add(movesLabel);
			panel[8].validate();
			switchNumbers(indexStartTile, indexFinishTile);
			if (isOver()) {
				playSound(AUDIO_FILE[9]);
				saveAScore(thumbnailNumbers[index - 1], numberOfMoves);
			}
		}

	}

	class ButtonDesign extends BasicButtonUI {

		@Override
		public void installUI(JComponent component) {
			super.installUI(component);
			AbstractButton abstractButton = (AbstractButton) component;
			abstractButton.setOpaque(false);
			abstractButton.setBorder(new EmptyBorder(5, 15, 5, 15));
		}

		@Override
		public void paint(Graphics graphics, JComponent component) {
			AbstractButton abstractButton = (AbstractButton) component;
			paintBackground(graphics, abstractButton, abstractButton.getModel().isPressed() ? 2 : 0);
			super.paint(graphics, component);
		}

		private void paintBackground(Graphics graphics, JComponent component, int yOffset) {
			Dimension size = component.getSize();
			Graphics2D graphics2D = (Graphics2D) graphics;
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setColor(component.getBackground().darker());
			graphics.fillRoundRect(0, yOffset, size.width, size.height - yOffset, 10, 10);
			graphics.setColor(component.getBackground());
			graphics.fillRoundRect(0, yOffset, size.width, size.height + yOffset - 5, 10, 10);
		}

	}

	class NumberDisplay extends BasicButtonUI {

		int number;

		public NumberDisplay(int number) {
			this.number = number;
		}

		public void paint(Graphics graphics, JComponent component) {
			super.paint(graphics, component);
			graphics.setFont(FONT_3);
			graphics.setColor(Color.RED);
			graphics.drawString(String.valueOf(number), 2, 14);
		}

	}

	public static void main(String[] args) {
		int totalNumberOfImages = getNumberOfImages(new File(FILE[1]));
		int[] thumbnailNumbers = getRandomNumbers(totalNumberOfImages);
		JFrame frame = new JFrame();
		frame.setTitle(NAME);
		frame.setSize(DIM_12);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.add(container);
		new FifteenPuzzle(container, thumbnailNumbers, totalNumberOfImages, 1, thumbnailNumbers[0], true, false, 0,
				false).display();
		frame.setVisible(true);
	}

}
