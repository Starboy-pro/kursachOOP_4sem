package edu.java.lab66;


import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.PrintWriter;

//Log4j
import org.apache.log4j.Logger;


/**
 * @author Kostin Andrey 0307
 * @version 1.00
 * 
 */

public class laba10 {
	
  // Объявления графических компонентов
	private JFrame app;
	private DefaultTableModel model;
	private JButton save,save_to_file, champ, call, soc,paper, search,add,delete,open_file;
	private JToolBar toolBar;
	private JScrollPane scroll;
	private JTable teams;
	private JComboBox tracks, month, top;
	private JTextField teamName, name;
	private Box box2;
	public static int flag = 15;
	private static final Logger log = Logger.getLogger("Lab10.class");

	
	//Обработка исключений
	private class MyException extends Exception {
				public MyException() {
				super ("Вы не ввели имя пилота!");
			}}
		  
	private void checkName (JTextField Name) throws MyException,NullPointerException
	{
		String sName = Name.getText();
		if (sName.contains("Имя пилота")) throw new MyException();
		if (sName.length() == 0) throw new NullPointerException();
	}
			
	private class MyException2 extends Exception {
		public MyException2() {
		super ("Вы не выбрали месяц!");
		}
	}
	
	private class MyException3 extends Exception {
		public MyException3() {
		super ("Вы не выбрали трассу!");
		}
	}
			
	private void checkMonth (JComboBox Month) throws MyException2
	{
		if (Month.getSelectedIndex() == 0) throw new MyException2();
	}
			
	private void checkTracks (JComboBox Tracks) throws MyException3
	{
		if (Tracks.getSelectedIndex() == 0) throw new MyException3();
	}
	
	//РАБОТА С ПОТОКАМИ 
			//Загрузка данных из XML-файла в экранную форму
		    Thread threat1 = new Thread(); // поток 1
		    //Редактирование данных и сохранение XML-файла
		    Thread threat2 = new Thread(); // поток 2
		    // Формирование отчета
		    Thread threat3 = new Thread(); // поток 3
		    
			// функция сохранения в XML
			public void save_to_xml()
			{
				try {
					// Создание парсера документа
					DocumentBuilder builder =
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
					// Создание пустого документа
					org.w3c.dom.Document doc = builder.newDocument();	
					Node racelist = doc.createElement("F1 manager");
					doc.appendChild(racelist);
					// Создание дочерних элементов group и присвоение значений атрибутам
					for (int i = 0; i < model.getRowCount(); i++)
					{
					Element race = doc.createElement("race");
					racelist.appendChild(race);
					race.setAttribute("team", (String)model.getValueAt(i, 0));
					race.setAttribute("racer", (String)model.getValueAt(i, 1));
					race.setAttribute("track", (String)model.getValueAt(i, 2));
					race.setAttribute("points", (String)model.getValueAt(i, 3));
				
					// Создание преобразователя документа
					Transformer trans = TransformerFactory.newInstance().newTransformer();
					// Создание файла с именем groups.xml для записи документа
					java.io.FileOutputStream fw = new FileOutputStream("races.xml");
					// Запись документа в файл
					trans.transform(new DOMSource(doc), new StreamResult(fw));
					}
				}
				catch (TransformerConfigurationException e) { e.printStackTrace(); }
				catch (TransformerException e) { e.printStackTrace(); }
				catch (IOException e) { e.printStackTrace(); }
				catch (ParserConfigurationException e) { e.printStackTrace(); }
			}
			
			//функция чтения данных из xml
			public void read_from_xml()
			{
				 try {
						int rows = model.getRowCount();
						for (int i = 0; i < rows; i++) model.removeRow(0); // Очистка таблицы
						// Создание парсера документа
						DocumentBuilder dBuilder =
						DocumentBuilderFactory.newInstance().newDocumentBuilder();
						// Чтение документа из файла
						org.w3c.dom.Document doc = dBuilder.newDocument();
						doc = dBuilder.parse(new File("races.xml"));
						// Нормализация документа
						doc.getDocumentElement().normalize();
						// Получение списка элементов с именем group
						NodeList nlBooks = doc.getElementsByTagName("race");
						// Цикл просмотра списка элементов и запись данных в таблицу
						for (int temp = 0; temp < nlBooks.getLength(); temp++)
						{
							// Выбор очередного элемента списка
							Node elem = nlBooks.item(temp);
							// Получение списка атрибутов элемента
							NamedNodeMap attrs = elem.getAttributes();
							// Чтение атрибутов элемента
							String team = attrs.getNamedItem("team").getNodeValue();
							String racer = attrs.getNamedItem("racer").getNodeValue();
							String track = attrs.getNamedItem("track").getNodeValue();
							String points = attrs.getNamedItem("points").getNodeValue();
							
							// Запись данных в таблицу
							model.addRow(new String[]{team,  racer, track, points});
						}
					}
					catch (ParserConfigurationException e) { e.printStackTrace(); }
					// Обработка ошибки парсера при чтении данных из XML-файла
					catch (SAXException e) { e.printStackTrace(); }
					catch (IOException e) { e.printStackTrace(); }
			}
			
			// функция сохранения отчета в pdf,html
			public void make_report() {
				Document document = new Document(PageSize.A4, 50, 50, 50, 50);

				PdfPTable t = new PdfPTable(4);
				try {

				PdfWriter.getInstance(document, new FileOutputStream("laba8.pdf"));
				} catch (FileNotFoundException e) {
				e.printStackTrace();
				} catch (DocumentException e) {
				e.printStackTrace();
				}

				BaseFont bfComic = null;

				try {
				bfComic =

				BaseFont.createFont("/Windows/Fonts/Arial.ttf" ,BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

				} catch (DocumentException e1) {
				e1.printStackTrace();
				log.warn("Error DocumentException", e1);
				} catch (IOException e1) {
				e1.printStackTrace();

				}
				Font font1 = new Font(bfComic, 12);
				t.addCell(new PdfPCell(new Phrase("Пилот", font1)));
				t.addCell(new PdfPCell(new Phrase("Команда", font1)));
				t.addCell(new PdfPCell(new Phrase("Трасса", font1)));
				t.addCell(new PdfPCell(new Phrase("Очки", font1)));
				for(int i = 0; i < model.getRowCount(); i++){
				t.addCell(new Phrase((String) model.getValueAt(i,0),font1));
				t.addCell(new Phrase((String) model.getValueAt(i,1),font1));
				t.addCell(new Phrase((String) model.getValueAt(i,2),font1));
				t.addCell(new Phrase((String) model.getValueAt(i,3),font1));
				}
				//document.open();
				try {
				document.add(t);
				} catch (DocumentException e) {
				e.printStackTrace();
				log.warn("Error DocumentException", e);
				}
				document.close();
				
					PrintWriter pw = null;
					try {
					pw = new PrintWriter(new FileWriter("laba8.html"));

					} catch (IOException e) {
					e.printStackTrace();
					}
					pw.println("<TABLE BORDER><TR><TH>Пилот<TH>Команда<TH>Трасса<TH>Очки</TR>");
					for(int i = 0; i < model.getRowCount(); i++) {
					pw.println("<TR><TD>" + (String) model.getValueAt(i,0) + "<TD>" + (String) model.getValueAt(i,1) + "<TD>" + (String) model.getValueAt(i,2) + "<TD>" + (String) model.getValueAt(i,3));
					}
					pw.close();
			}


	public void show() {
		// Создание окна 
		app = new JFrame("F1 manager"); //заголовок программы
		app.setSize(1200, 700); //Отвечает за размеры окна
		app.setLocation(100, 100); //Отвечает за расположение окна
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Создание кнопок и прикрепление иконок
		save = new JButton(new ImageIcon("C:/Users/andrey/eclipse-workspace/Lab2/src/images/save.png"));
		save.setPreferredSize(new Dimension(10, 40));
		champ = new JButton(new ImageIcon("C:/Users/andrey/eclipse-workspace/Lab2/src/images/champ.png"));
		champ.setPreferredSize(new Dimension(10, 40));
		call = new JButton(new ImageIcon("C:/Users/andrey/eclipse-workspace/Lab2/src/images/call.png"));
		call.setPreferredSize(new Dimension(10, 40));
		soc = new JButton(new ImageIcon("C:/Users/andrey/eclipse-workspace/Lab2/src/images/soc.png"));
		soc.setPreferredSize(new Dimension(10, 40));
		add = new JButton(new ImageIcon("C:/Users/andrey/eclipse-workspace/Lab2/src/images/add.png"));
		add.setPreferredSize(new Dimension(10, 40));
		delete = new JButton(new ImageIcon("C:/Users/andrey/eclipse-workspace/Lab2/src/images/delete.png"));
		delete.setPreferredSize(new Dimension(10, 40));
		open_file = new JButton(new ImageIcon("C:/Users/andrey/eclipse-workspace/Lab2/src/images/open_file.png"));
		open_file.setPreferredSize(new Dimension(10, 40));
		save_to_file = new JButton(new ImageIcon("C:/Users/andrey/eclipse-workspace/Lab2/src/images/save_to_file.png"));
		save_to_file.setPreferredSize(new Dimension(10, 40));
      
      
	    


            // Настройка подсказок для кнопок
		save.setToolTipText("Сохранить изменения");
		add.setToolTipText("Добавить");
		delete.setToolTipText("Удалить");
		open_file.setToolTipText("Открыть файл");
		save_to_file.setToolTipText("Сохранить в файл");
		champ.setToolTipText("<HTML>Лучший: L. Hamilton - 68");
		call.setToolTipText("<HTML>Контакты менеджера: <br>Майкл <br>Телефон: +16214587757<br>E-mail: F1_manager@mail.ru");
		soc.setToolTipText("<HTML>Наши соцсети: <br>Twitter: @f1manager <br>Instagram: @F1_manager");
		

		
		// Добавление кнопок на панель инструментов
		toolBar = new JToolBar("Панель инструментов");
		toolBar.add(save);
		toolBar.add(add);
		toolBar.add(delete);
		toolBar.add(open_file);
		toolBar.add(save_to_file);
		toolBar.addSeparator(new Dimension(900, 40));
		toolBar.add(champ);
		toolBar.add(soc);
		toolBar.add(call);
		
		
		// Размещение панели инструментов
		app.setLayout(new BorderLayout());
		app.add(toolBar, BorderLayout.NORTH);
		
		// Создание таблицы с данными
		String [] columns = {"Команда", "Пилот", "Гран-при и позиция","Очки"};
		String [][] data = {{"Mercedes", "L.Hamilton", "<HTML>28/03:Сахир(Бахрейн) - 1 место<br>18/04:Имола(Италия) - 2 место<br>2/05:Портимао(Португалия) - 1 место","<HTML>68"}, {"Red Bull Racing", "M.Verstappen", "<HTML>28/03:Сахир(Бахрейн) - 2 место<br>18/04:Имола(Италия) - 1 место<br>2/05:Портимао(Португалия) - 2 место","<HTML>61"}, {"Ferrari", "C.Sainz", "<HTML>28/03:Сахир(Бахрейн) - 8 место<br>18/04:Имола(Италия) - 5 место<br>2/05:Портимао(Португалия) - 11 место","<HTML>14"}};
		model= new DefaultTableModel(data, columns);
		teams = new JTable(model);
		scroll = new JScrollPane(teams);
		teams.setRowHeight(150);
		
		// Размещение таблицы с данными
		app.add(scroll, BorderLayout.CENTER);
		
		month = new JComboBox(new String[]{"Месяц","Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь", "Октябрь", "Ноябрь","Декабрь"});
		tracks = new JComboBox(new String[]{"Трассы","Сахир(Бахрейн)","Имола(Италия)","Портимао(Португалия)","Барселона(Испания)","Монако","Баку(Азербайджан)","Поль Рикар(Франция)","Шпильберг(Австрия)","Сильверстоун(Великобритания)", "Хунгароринг(Венгрия)", "Спа(Бельгия)","Зандфорт(Нидерланды)","Монца(Италия)","Сочи(Россия)","Стамбул(Турция)","Остин(США)","Мехико(Мексика)","Сан-Паулу(Бразилия","Лосаил(Катар)","Джидда(Саудовская-Аравия","Яс-Марина(ОАЭ)"});



		paper = new JButton("На главную");
		name = new JTextField("Имя пилота");
		search = new JButton("Поиск");
		
		// Добавление компонентов на панель
		JPanel filterPanel = new JPanel();
		
		filterPanel.add(paper);
		filterPanel.add(month);
		filterPanel.add(tracks);
		filterPanel.add(name);
		filterPanel.add(search);
	
		
		// Размещение панели поиска внизу окна
		app.add(filterPanel, BorderLayout.SOUTH);
		// Визуализация экранной формы
		app.setVisible(true);

		
		// Визуализация экранной формы
		app.setVisible(true);
	
	
	//Добавление слушателей
	
	//нажатие на кнопки
	champ.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent event)
			{
			JOptionPane.showMessageDialog (app,"<HTML>Лидер чемпионата: <br> Lewis Hamilton - 68 очков" );
			}
	});
	

	soc.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent event)
		{
		JOptionPane.showMessageDialog (app,"<HTML>Наши соцсети: <br>Twitter: @f1manager <br>Instagram: @F1_manager");
		}
	});
	
	call.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent event)
		{
		JOptionPane.showMessageDialog (app, "<HTML>Контакты менеджера: <br>Майкл <br>Телефон: +16214587757<br>E-mail: F1_manager@gmail.com");
		}
	});
	
	save.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent event)
		{
			threat3 = new Thread(() -> {
                if ((threat2.isAlive())||(threat1.isAlive())) {
                    try {
                        JOptionPane.showMessageDialog(app, "Waiting for thread 2 to finish");
                        threat2.join();
                        JOptionPane.showMessageDialog(app, "2 thread worked");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                if (model.getRowCount() != 0) {
                    JOptionPane.showMessageDialog(null, "3rd thread creates a report");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                make_report();
            });
            threat3.start();
        	
		}
	});

	/*save_to_file.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent event)
		{
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);

			PdfPTable t = new PdfPTable(4);
			try {

			PdfWriter.getInstance(document, new FileOutputStream("laba7.pdf"));
			} catch (FileNotFoundException e) {
			e.printStackTrace();
			} catch (DocumentException e) {
			e.printStackTrace();
			}

			BaseFont bfComic = null;

			try {
			bfComic =

			BaseFont.createFont("/Windows/Fonts/Arial.ttf" ,BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

			} catch (DocumentException e1) {
			e1.printStackTrace();
			} catch (IOException e1) {
			e1.printStackTrace();

			}
			Font font1 = new Font(bfComic, 12);
			t.addCell(new PdfPCell(new Phrase("Пилот", font1)));
			t.addCell(new PdfPCell(new Phrase("Команда", font1)));
			t.addCell(new PdfPCell(new Phrase("Трасса", font1)));
			t.addCell(new PdfPCell(new Phrase("Очки", font1)));
			for(int i = 0; i < model.getRowCount(); i++){
			t.addCell(new Phrase((String) model.getValueAt(i,0),font1));
			t.addCell(new Phrase((String) model.getValueAt(i,1),font1));
			t.addCell(new Phrase((String) model.getValueAt(i,2),font1));
			t.addCell(new Phrase((String) model.getValueAt(i,3),font1));
			}
			document.open();
			try {
			document.add(t);
			} catch (DocumentException e) {
			e.printStackTrace();
			}
			document.close();
			
				PrintWriter pw = null;
				try {
				pw = new PrintWriter(new FileWriter("laba7.html"));

				} catch (IOException e) {
				e.printStackTrace();
				}
				pw.println("<TABLE BORDER><TR><TH>Пилот<TH>Команда<TH>Трасса<TH>Очки</TR>");
				for(int i = 0; i < model.getRowCount(); i++) {
				pw.println("<TR><TD>" + (String) model.getValueAt(i,0) + "<TD>" + (String) model.getValueAt(i,1) + "<TD>" + (String) model.getValueAt(i,2) + "<TD>" + (String) model.getValueAt(i,3));
				}
				pw.close();
		}
	});*/


	
	//добавление новой строки
	
	add.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent event)
		{
			model.addRow(new Object[]{"Ferrari", "C.Leclerc", "<HTML>28/03:Сахир(Бахрейн) - 6 место<br>18/04:Имола(Италия) - 4 место<br>2/05:Портимао(Португалия) - 6 место","<HTML>28"});
			
		}
	});
	
	//удаление последней строки таблицы
	
	delete.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent event)
		{
			model.setRowCount(model.getRowCount()- 1);
		}
	});
	
	open_file.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent event)
		{
			
			threat1 = new Thread(() -> {
                JOptionPane.showMessageDialog(app, "1 thread started");
                read_from_xml();
  
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(app, "1 thread finished");
                });
            });
            threat1.start();	
		}
	});


	save_to_file.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent event)
		{
			
			threat2 = new Thread(() -> {
                if (threat1.isAlive()) {
                    try {
                        JOptionPane.showMessageDialog(app, "Waiting for thread 1 to finish");
                        threat1.join();
                        JOptionPane.showMessageDialog(app, "1 thread worked");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                if (model.getRowCount() != 0) {
                    JOptionPane.showMessageDialog(null, "2nd thread saves a xml");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                save_to_xml();
            });
            threat2.start();
		}
	});


	
	//возврат к исходной таблице
	paper.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent event)
		{
			model.setColumnCount(0);
			model.addColumn("Команда");
			model.addColumn("Пилот");
			model.addColumn("Гран-при и позиция");
			model.addColumn("Очки");
			model.setRowCount(0);
			model.addRow(new Object[]{"Mercedes", "L.Hamilton", "28/03:Sahir(Bahrain)","68"});
			model.addRow(new Object[]{"Red Bull Racing", "M.Verstappen", "28/03:Sahir(Bahrain)","61"});
			model.addRow(new Object[]{"Ferrari", "C.Sainz", "28/03:Sahir(Bahrain)","14"});
			
		}
		
	});
	
	
	search.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
		try { checkName(name);
		}
		catch(NullPointerException ex) {
		JOptionPane.showMessageDialog(app, ex.toString());
		}
		catch(MyException myEx) {
		JOptionPane.showMessageDialog(null, myEx.getMessage());
		}}});

	

	//изменение таблицы по месяцам
	month.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent event) {
			
			model.setColumnCount(0);
			model.addColumn("Дата проведения");
			model.addColumn("Трасса");
			model.addColumn("Победитель");
			model.setRowCount(0);
			
			//Обновление таблицы с данными (с проверкой на корректность ввода)
			try {
				checkMonth(month);
				switch(month.getSelectedIndex()) {
					
						case 1:
							model.addRow(new Object[]{"----------", "ПЕРЕРЫВ МЕЖДУ СЕЗОНАМИ", "----------"});
							
							break;
						case 2:
							model.addRow(new Object[]{"----------", "ПЕРЕРЫВ МЕЖДУ СЕЗОНАМИ", "----------"});
							
							break;
						case 3:
							model.addRow(new Object[]{"28/03", "Сахир(Бахрейн)", "L.Hamilton"});
							
							break;
						case 4:
							model.addRow(new Object[]{"18/04", "Имола(Италия)", "M.Verstappen"});
							
							break;
						case 5:
							model.addRow(new Object[]{"2/05", "Портимао(Португалия)", "L.Hamilton"});
							model.addRow(new Object[]{"9/05", "Барселона(Испания)", "L.Hamilton"});
							model.addRow(new Object[]{"23/05", "Монако", "M.Verstappen"});
							
							break;
						case 6:
							model.addRow(new Object[]{"6/06", "Баку(Азербайджан)", "S.Perez"});
							model.addRow(new Object[]{"20/06", "Поль Рикар(Франция)", "M.Verstappen"});
							model.addRow(new Object[]{"27/06", "Штирия(Австрия)", "M.Verstappen"});
							
							break;
						case 7:
							model.addRow(new Object[]{"4/07", "Шпильберг(Австрия)", "M.Verstappen"});
							model.addRow(new Object[]{"18/07", "Сильверстоун(Великобритания)", "L.Hamilton"});
							
						case 8:
							model.addRow(new Object[]{"1/08", "Хунгароринг(Венгрия)", "E.Ocon"});
							model.addRow(new Object[]{"29/08", "Спа(Бельгия)", "M.Verstappen"});
							
							break;
						case 9:
							model.addRow(new Object[]{"5/09", "Зандфорт(Нидерланды)", "M.Verstappen"});
							model.addRow(new Object[]{"12/09", "Монца(италия)", "D.Ricciardo"});
							model.addRow(new Object[]{"26/09", "Сочи(Италия)","L.Hamilton"}); 
							break;
						case 10:
							
							model.addRow(new Object[]{"10/10", "Стамбул(Турция)", "V.Bottas"});
							model.addRow(new Object[]{"24/10", "Остин(США)", "M.Verstappen"});
						case 11:
							
							model.addRow(new Object[]{"7/11", "Мехико(Мексика)", "M.Verstappen"});
							model.addRow(new Object[]{"14/11", "Сан-Паулу(Бразилия)", "L.Hamilton"});
							model.addRow(new Object[]{"21/11", "Лосиал(Катар)", "L.Hamilton"});
							break;
						case 12:
							
							model.addRow(new Object[]{"5/12", "Джидда(Саудовская Аравия)", "L.Hamilton"});
							model.addRow(new Object[]{"12/12", "Абу-Даби(ОАЭ)", "M.Verstappen"});
							break;
					}}
					catch(MyException2 myEx) {
						JOptionPane.showMessageDialog(null, myEx.getMessage());
						log.debug("The month was not selected!");
					}

			}
			
	});


	
	//изменение таблицы с отчетами 
	tracks.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent event) {
			
			model.setColumnCount(0);
			model.addColumn("Пилот");
			model.addColumn("Команда");
			model.addColumn("Время");
			model.addColumn("Место");
			model.setRowCount(0);
			
			//Обновление таблицы с данными (с проверкой на корректность ввода)
			try {
				checkTracks(tracks);
				switch(tracks.getSelectedIndex()) {
				    
					case 1:
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 2:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "+22.000s", "2"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "	2:02:34.598", "1"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+27.036s","5"});
						break;
						
					case 3:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:34:31.421", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+29.148s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+78.955s","11"});
						break;
						
					case 4:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:33:07.680", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+15.841s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+74.670s","7"});
						break;
						
					case 5:
					
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "+68.231s", "7"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "1:38:56.820", "1"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+8.968s","2"});
						break;
	
						
					case 6:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "+68.231s", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
	
						
					case 7:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
					
					case 8:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 9:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
					
					case 10:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 11:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
	
						
					case 12:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 13:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 14:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 15:
		
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
		
					case 16:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 17:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 18:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 19:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 20:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 21:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
						
					case 22:
						
						model.addRow(new Object[]{"L.Hamilton", "Mercedes", "1:32:03.897", "1"});
						model.addRow(new Object[]{"M.Verstappen", "Red Bull Racing", "+0.745s", "2"});
						model.addRow(new Object[]{"C.Sainz", "Ferrari", "+67.100s","8"});
						break;
				}}
				catch(MyException3 myEx) {
					JOptionPane.showMessageDialog(null, myEx.getMessage());
					log.debug("The track was not selected!");
				}

		}
	});
			
}

		
	/**
	 * @param args the command line arguments
	 */
	
	public static void main(String[] args) {
		log.info("Start app");
		// Создание и отображение экранной формы
			new laba10().show();
		log.info("Finish app");
	}
}
