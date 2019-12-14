package sample;

import h2.H2;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.springframework.util.StringUtils;
import service.SaveService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;

public class Controller {

    private H2 h2 = new H2();

    private SaveService saveService = new SaveService();

    @FXML
    public TextField textField;

    @FXML
    public ListView<String> listView;

    @FXML
    public void search() throws Exception {
        //拿到搜索框数据
        String text = textField.getText();
        List list;
        if (StringUtils.isEmpty(text)){
            //展示全部
            list = h2.findAll();
        }else {
            //在h2数据库中搜索数据 精确搜索
            list = h2.findList(text);
        }
        //显示在搜索结果页面
        ObservableList<String> result = FXCollections.observableArrayList(list);
        listView.setItems(result);
    }

    @FXML
    public void upload() throws Exception {
//        读取文本数据
        LoadFile loadFile = new LoadFile();
        loadFile.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        result = loadFile.showSelect();

    }


    int result;
    //上传文件窗口 点击x关闭有bug会退出整个系统
    public class LoadFile extends JFrame {


        private JTextField textField;
        private JPanel panel = new JPanel();
        private JFileChooser fileChooser = new JFileChooser();
        private File selectedFile = null;



        public int showSelect() {


            int i = fileChooser.showOpenDialog(getContentPane());
            // 判断用户单击的是否为“打开”按钮
            if (i == JFileChooser.APPROVE_OPTION) {

                selectedFile = fileChooser.getSelectedFile();// 获得选中的文件对象
                textField.setText(selectedFile.getName());// 显示选中文件的名称
            }
            return i;
        }

        public LoadFile() {
            setTitle("选项卡面板");
            setBounds(400, 400, 400, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            final JLabel label = new JLabel();
            label.setText("文件：");
            panel.add(label);

            textField = new JTextField();
            textField.setColumns(20);
            panel.add(textField);


            final JButton button = new JButton("上传");
            button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    if (selectedFile != null) {
                        JOptionPane.showMessageDialog(null, "上传途中不要关闭软件，否则可能造成数据丢失，点击确定开始长传，等待提示");
                        try {
                            saveService.save(selectedFile);
                            JOptionPane.showMessageDialog(null, "上传成功");
                            dispose();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(null, "上传失败 \n"+e1.getMessage());
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "请重新选择文件");
                    }
                }
            });
            panel.add(button);


            add(panel, BorderLayout.NORTH);
            setVisible(true);
        }

    }

}
