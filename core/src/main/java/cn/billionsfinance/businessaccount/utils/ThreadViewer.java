package cn.billionsfinance.businessaccount.utils;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Created by hanlin.huang on 2017/4/24.
 */
public class ThreadViewer extends JPanel {

    private ThreadViewerTableModel tableModel;
    public ThreadViewer() {
        tableModel = new ThreadViewerTableModel();
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel colModel = table.getColumnModel();
        int numColumns = colModel.getColumnCount();
        // manually size all but the last column
        for ( int i = 0; i < numColumns - 1; i++ ) {
            TableColumn col = colModel.getColumn(i);
            col.sizeWidthToFit();
            col.setPreferredWidth(col.getWidth() + 5);
            col.setMaxWidth(col.getWidth() + 5);
        }
        JScrollPane sp = new JScrollPane(table);
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
    }
    public void dispose() {
        tableModel.stopRequest();
    }
    protected void finalize() throws Throwable {
        dispose();
    }
    public static JFrame createFramedInstance() {
        final ThreadViewer viewer = new ThreadViewer();
        final JFrame f = new JFrame("线程动态查询");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                f.setVisible(false);
                f.dispose();
                viewer.dispose();
            }
        });
        f.setContentPane(viewer);
        int width = 700;
        int height = 500;
        f.setSize(width, height);
        //f.setName("");
        int w = (Toolkit.getDefaultToolkit().getScreenSize().width - width) / 2;
        int h = (Toolkit.getDefaultToolkit().getScreenSize().height - height) / 2;
        f.setLocation(w, h);

        f.setVisible(true);
        return f;
    }




    public static void showThreads() {
        JFrame f = ThreadViewer.createFramedInstance();
        // For this example, exit the VM when the viewer
        // frame is closed.
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        // Keep the main thread from exiting by blocking
        // on wait() for a notification that never comes.
        Object lock = new Object();
        synchronized ( lock ) {
            try {
                lock.wait();
            } catch ( InterruptedException x ) {
            }
        }
    }

    public static void main(String[] args) {
        showThreads();
    }
}
