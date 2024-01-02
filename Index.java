import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

class DB
{
    static final String URL = "jdbc:mysql://localhost:3306/Library";
    static final String user = "root";
    static final String pass = "2004";
    static Connection conn;
    static Statement stmt;

    static void setUp()
    {
        try{
            conn = DriverManager.getConnection(URL,user,pass);
            stmt = conn.createStatement();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }

        try{
            String q = "select max(B_id) from Books";
            ResultSet r = stmt.executeQuery(q);
            r.next();
            Books.ID = r.getInt(1)+1;
            r.close();    
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    static ResultSet select(String q)
    {
        try{
            ResultSet r = stmt.executeQuery(q);
            return r;
        }
        catch(SQLException e)
        {
            return null;
        }
    }

    static void insert(String q)
    {
        try{
            stmt.executeUpdate(q);
        }
        catch(SQLException e)
        {
            System.out.println("unable to execute : "+q);
        }
    }
}

class Main extends JFrame implements ActionListener
{
    JLabel title;
    JButton book_add;
    JButton book_delete;
    JButton book_search;
    JButton book_issue;
    JButton book_return;
    static boolean open = true;

    public Main()
    {
        super("Welcome");
        this.title = new JLabel("What would you like to do?",JLabel.CENTER);
        this.setLayout(new GridLayout(2,0));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(title);
        JPanel p = new JPanel();
        book_add = new JButton("ADD BOOK");
        book_delete = new JButton("REMOVE BOOK");
        book_search = new JButton("FIND BOOK");
        book_issue = new JButton("ISSUE BOOK");
        book_return = new JButton("RETURN BOOK");

        book_add.addActionListener(this);
        book_delete.addActionListener(this);
        book_search.addActionListener(this);
        book_issue.addActionListener(this);
        book_return.addActionListener(this);

        p.setLayout(new GridLayout(3,0));
        p.add(book_add);
        p.add(book_search);
        p.add(book_issue);
        p.add(book_return);
        p.add(book_delete);
        this.add(p);

        setSize(400,200);
        this.setVisible(true);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if(e.getSource() == book_add && open)
        {
            new Add_book();
            open = false;
        }
        else if(e.getSource() == book_delete && open)
        {
            new Delete_book();
            open = false;
        }
        else if(e.getSource() == book_search && open)
        {
            new Search_book();
            open = false;
        }
        else if(e.getSource() == book_issue && open)
        {
            new Issue_book();
            open = false;
        }
        else if(e.getSource() == book_return && open)
        {
            new Return_book();
            open = false;
        }

    }
}

class Cal
{
    static Date date;

    static Date getDate()
    {
        
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH)+1;
        int d = c.get(Calendar.DATE);
        String s = y+"-"+m+"-"+d;
        Date date = Date.valueOf(s);
        return date;
    }

    static Date getDate(int x)
    {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,x);
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH)+1;
        int d = c.get(Calendar.DATE);
        String s = y+"-"+m+"-"+d;
        Date date = Date.valueOf(s);
        return date;
    }

    static int difference(Date i,Date r)
    {
        LocalDate li = i.toLocalDate();
        LocalDate lr = r.toLocalDate();
        Period diff = Period.between(li, lr);
        return diff.getDays();
    }
}

class Books
{
    int B_id;
    String B_name;
    String B_author;
    int B_copies;
    int B_av_copies;
    static int ID;

    public Books(String name,String author, int n)
    {
        B_id = ID;
        B_name = name;
        B_author = author;
        B_copies = n;
        B_av_copies = n;
        ID++;
    }
}

class Books_issue
{
    int B_id;
    int S_id;
    Date Issued_date;
    Date Expected_return;
    Date Return_date;
    int Fine;
    int days=15;
    int p = 1;

    public Books_issue(int bid , int sid)
    {
        this.B_id = bid;
        this.S_id = sid;
        this.Issued_date = Cal.getDate();
        this.Expected_return = Cal.getDate(days);
        this.Return_date = null;
        Fine = 0;
    }

    public Books_issue(int bid, int sid, Date ex)
    {
        B_id = bid;
        S_id = sid;
        Expected_return = ex;
        Return_date = Cal.getDate();
        int d = Cal.difference(Expected_return, Return_date);
        Fine = p*d;
    }
}

class Add_book extends JFrame implements ActionListener,WindowListener
{
    JButton newbook;
    JButton existingbook;

    public Add_book()
    {
        setTitle("Add Books To Lirary");
        //setLocationRelativeTo(null);
        this.addWindowListener(this);
        setLayout(new GridLayout(2,0));
        JLabel la = new JLabel("Choose an appropriate option.",JLabel.CENTER);
        add(la);
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2,0));
        newbook = new JButton("ADD NEW BOOK");
        existingbook = new JButton("ADD COPIES OF EXISTING BOOKS");
        newbook.addActionListener(this);
        existingbook.addActionListener(this);
        p.add(newbook);
        p.add(existingbook);
        add(p);
        setSize(400,200);
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if(e.getSource()==newbook)
        {
            this.dispose();
            new Add_new_book();
        }
            
        else if(e.getSource() == existingbook)
        {
            this.dispose();
            new Add_existing_book();  
        }
          
    }

     @Override
    public void windowClosing(WindowEvent e) {
        dispose();
        Main.open = true;
    }

     @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}

class Add_new_book extends JFrame implements ActionListener, WindowListener
{
    JLabel bname, bauthor,n;
    JTextField tbname,tbauthor,tn;
    JButton submit;
    JButton cancel;

    public Add_new_book()
    {
        setTitle("Add Books To Library");
        this.addWindowListener(this);
        setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(3,3));
        bname = new JLabel("  Enter book name : ",JLabel.LEFT);
        p1.add(bname);
        tbname = new JTextField(30);
        tbname.setEditable(true);
        p1.add(tbname);
        bauthor = new JLabel("  Enter the name of author : ",JLabel.LEFT);
        p1.add(bauthor);
        tbauthor = new JTextField(30);
        tbauthor.setEditable(true);
        p1.add(tbauthor);
        n = new JLabel("Enter the number of copies : ",JLabel.LEFT);
        p1.add(n);
        tn = new JTextField(3);
        tn.setEditable(true);
        p1.add(tn);
        add(p1);
        JPanel p2 = new JPanel();
        submit = new JButton("ADD BOOK");
        submit.addActionListener(this);
        p2.add(submit);
        add("South",p2);
        setSize(800,200);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if(!(tbname.getText().equals("") || tbauthor.getText().equals("") ||tn.getText().equals("") || Integer.parseInt(tn.getText())<=0))
        {
            Books b = new Books(tbname.getText(), tbauthor.getText(), Integer.parseInt(tn.getText()));
            String q = "insert into Books values ("+b.B_id+",'"+b.B_name+"','"+b.B_author+"',"+b.B_copies+","+b.B_av_copies+")";
            DB.insert(q);
            this.dispose();
            JOptionPane.showMessageDialog(null,"The Book ID is "+b.B_id+".","Book Added Successfully" ,JOptionPane.PLAIN_MESSAGE);
            Main.open = true;
        }
        else
        {
            JOptionPane.showMessageDialog(null, "!!PLEASE ENTER VALID DATA!!");
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        dispose();
        Main.open = true;
    }

     @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
    
}

class Add_existing_book extends JFrame implements ActionListener, WindowListener
{
    JLabel bid, n;
    JTextField tbid,tn;
    JButton submit;

    public Add_existing_book()
    {
        setTitle("Add Books To Library");
        addWindowListener(this);
        setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(2,2));
        bid = new JLabel("Enter Book ID : ",JLabel.LEFT);
        p1.add(bid);
        tbid = new JTextField(3);
        tbid.setEditable(true);
        p1.add(tbid);
        n = new JLabel("Enter number of copies : ",JLabel.LEFT);
        p1.add(n);
        tn = new JTextField(3);
        tn.setEditable(true);
        p1.add(tn);
        add(p1);
        JPanel p2 = new JPanel();
        submit = new JButton("ADD RECORD");
        submit.addActionListener(this);
        p2.add(submit);
        add("South",p2);
        setSize(800,150);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(!(tbid.getText().equals("") || tn.getText().equals("") || Integer.parseInt(tbid.getText()) <=0 || Integer.parseInt(tn.getText())<=0))
        {
            String q = "update Books set B_copies = B_copies+"+Integer.parseInt(tn.getText())+" , B_av_copies = B_av_copies+"+Integer.parseInt(tn.getText())+" where B_id ="+Integer.parseInt(tbid.getText());
            DB.insert(q);
            this.dispose();
            JOptionPane.showMessageDialog(null,"Record Updated Successfully.");
            Main.open = true;
        }
        else
        {
            JOptionPane.showMessageDialog(null, "!!PLEASE ENTER VALID DATA!!");   
        }
    }

    @Override
    public void windowClosing(WindowEvent e) 
    {
        dispose();
        Main.open = true;
    }

     @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
    

}

class Delete_book extends JFrame implements ActionListener,WindowListener
{
    JLabel bid;
    JTextField tbid;
    JButton submit;

    public Delete_book()
    {
        setTitle("Delete Records");
        
        addWindowListener(this);
        setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(1,0));
        bid = new JLabel("Enter the Book ID : ",JLabel.LEFT);
        p1.add(bid);
        tbid = new JTextField(3);
        tbid.setEditable(true);
        p1.add(tbid);
        add(p1);
        JPanel p2 = new JPanel();
        submit = new JButton("DELETE");
        submit.addActionListener(this);
        p2.add(submit);
        add("South",p2);
        setSize(800,100);
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(!(tbid.getText().equals("")|| Integer.parseInt(tbid.getText())<=0))
        {
            String q = "delete from Books where B_id = "+Integer.parseInt(tbid.getText());
            DB.insert(q);
            q = "delete from Books_issue where B_id = "+Integer.parseInt(tbid.getText());
            DB.insert(q);
            this.dispose();
            JOptionPane.showMessageDialog(null, "RECORD DELETED SUCCESSFULLY");
            Main.open = true;
        }
        else
        {
            JOptionPane.showMessageDialog(null, "!!PLEASE ENTER VALID DATA!!");   
        }
    }

    @Override
    public void windowClosing(WindowEvent e) 
    {
        dispose();
        Main.open = true;
    }

     @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
    

}

class Search_book extends JFrame implements ActionListener,WindowListener
{
    JButton bid,bname,bauthor;

    public Search_book()
    {
        setTitle("Search");
        
        addWindowListener(this);
        setLayout(new GridLayout(3,0));
        bid = new JButton("Search with Book ID");
        bid.addActionListener(this);
        add(bid);
        bname = new JButton("Search with Book Name");
        bname.addActionListener(this);
        add(bname);
        bauthor = new JButton("Search by Author Name");
        bauthor.addActionListener(this);
        add(bauthor);
        setSize(400,150);
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == bid)
        {
            new Bid();
            this.dispose();
        }
        else if(e.getSource() == bname)
        {

            new Bname();
            this.dispose();
        }
        else if(e.getSource() == bauthor)
        {
            new Bauthor();
            this.dispose();
        }
    }

    @Override
    public void windowClosing(WindowEvent e) 
    {
        dispose();
        Main.open = true;
    }

     @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}

class Bid extends JFrame implements ActionListener,WindowListener
{
    JLabel bid;
    JTextField tbid;
    JButton submit;

    public Bid()
    {
        setTitle("Search Records");
        
        addWindowListener(this);
        setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(1,0));
        bid = new JLabel("Enter the Book ID : ",JLabel.LEFT);
        p1.add(bid);
        tbid = new JTextField(3);
        tbid.setEditable(true);
        p1.add(tbid);
        add(p1);
        JPanel p2 = new JPanel();
        submit = new JButton("SEARCH");
        submit.addActionListener(this);
        p2.add(submit);
        add("South",p2);
        setSize(800,100);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(!(tbid.getText().equals("")))
        {
            String q = "select * from Books where B_id = "+tbid.getText();
            ResultSet r = DB.select(q); 
            new Display(r);
            dispose();
        }
        else
        {
            JOptionPane.showMessageDialog(null, "!!PLEASE ENTER VALID DATA!!");
        }
    }

    @Override
    public void windowClosing(WindowEvent e) 
    {
        dispose();
        Main.open = true;
    }

     @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}

class Bname extends JFrame implements ActionListener,WindowListener
{
    JLabel bname;
    JTextField tbname;
    JButton submit;

    public Bname()
    {
        setTitle("Search Records");
        
        addWindowListener(this);
        setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(1,0));
        bname = new JLabel("Enter the Book Name : ",JLabel.LEFT);
        p1.add(bname);
        tbname = new JTextField(3);
        tbname.setEditable(true);
        p1.add(tbname);
        add(p1);
        JPanel p2 = new JPanel();
        submit = new JButton("SEARCH");
        submit.addActionListener(this);
        p2.add(submit);
        add("South",p2);
        setSize(800,100);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(!(tbname.getText().equals("")))
        {
            String q = "select * from Books where B_name = '"+tbname.getText()+"'";
            ResultSet r = DB.select(q); 
            new Display(r);
            dispose();
        }
        else
        {
            JOptionPane.showMessageDialog(null, "!!PLEASE ENTER VALID DATA!!");
        }
    }

    @Override
    public void windowClosing(WindowEvent e) 
    {
        dispose();
        Main.open = true;
    }

     @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}

class Bauthor extends JFrame implements ActionListener,WindowListener
{
    JLabel bauthor;
    JTextField tbauthor;
    JButton submit;

    public Bauthor()
    {
        setTitle("Search Records");
        
        addWindowListener(this);
        setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(1,0));
        bauthor = new JLabel("Enter the name of Author : ",JLabel.LEFT);
        p1.add(bauthor);
        tbauthor = new JTextField(3);
        tbauthor.setEditable(true);
        p1.add(tbauthor);
        add(p1);
        JPanel p2 = new JPanel();
        submit = new JButton("SEARCH");
        submit.addActionListener(this);
        p2.add(submit);
        add("South",p2);
        setSize(800,100);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(!(tbauthor.getText().equals("")))
        {
            String q = "select * from Books where B_author = '"+tbauthor.getText()+"'";
            ResultSet r = DB.select(q); 
            new Display(r);
            dispose();
        }
        else
        {
            JOptionPane.showMessageDialog(null, "!!PLEASE ENTER VALID DATA!!");
        }
    }

    @Override
    public void windowClosing(WindowEvent e) 
    {
        dispose();
        Main.open = true;
    }

    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}

class Display extends JFrame
{

    public Display(ResultSet r)
    {
        this.setLayout(new GridLayout(0,1));
        boolean flag = false;
        

        try{
            while(r.next())
            {
                JPanel p1 = new JPanel();
                p1.setLayout(new GridLayout(0,2));
                JLabel bid,bname,bauth,n;
                JTextField tbid,tbname,tbauth,tn;
                p1.setBorder(new LineBorder(Color.BLACK,2));
                bid = new JLabel("Book ID : ");
                p1.add(bid);
                tbid = new JTextField(r.getString(1));
                tbid.setEditable(false);
                p1.add(tbid);
                bname = new JLabel("Book Name : ");
                p1.add(bname);
                tbname = new JTextField(r.getString(2));
                tbname.setEditable(false);
                p1.add(tbname);
                bauth = new JLabel("Book Author : ");
                p1.add(bauth);
                tbauth = new JTextField(r.getString(3));
                tbauth.setEditable(false);
                p1.add(tbauth);
                n = new JLabel("Total Number of copies : ");
                p1.add(n);
                tn = new JTextField(r.getString(4));
                tn.setEditable(false);
                p1.add(tn);
                JLabel an = new JLabel("Available Number of Copies : ");
                p1.add(an);
                JTextField tan = new JTextField(r.getString(5));
                tan.setEditable(false);
                p1.add(tan);
                this.add(p1);
                flag = false;
                
            }

            if(flag)
            {
                add(new JLabel("No Records Found",JLabel.CENTER));
            }

            //setSize(400,200);
            pack();
            setVisible(true);
            Main.open = true;
            r.close();

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
}

class Issue_book extends JFrame implements ActionListener, WindowListener
{
    JLabel bid,sid;
    JTextField tbid,tsid;
    JButton submit;

    public Issue_book()
    {
        setTitle("Issue Book");
        addWindowListener(this);
        setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(2,0));
        bid = new JLabel("Enter Book ID : ",JLabel.LEFT);
        p1.add(bid);
        tbid = new JTextField(3);
        tbid.setEditable(true);
        p1.add(tbid);
        sid = new JLabel("Enter Student ID : ",JLabel.LEFT);
        p1.add(sid);
        tsid = new JTextField(3);
        tsid.setEditable(true);
        p1.add(tsid);
        add(p1);
        JPanel p2 = new JPanel();
        submit = new JButton("ISSUE BOOK");
        submit.addActionListener(this);
        p2.add(submit);
        add("South",p2);
        setSize(800,150);
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(!(tbid.getText().equals("") || tsid.getText().equals("") || Integer.parseInt(tbid.getText())<=0 || Integer.parseInt(tsid.getText())<=0))
        {
            String q = "Select B_av_copies from Books where B_id = "+tbid.getText();
            ResultSet r = DB.select(q);
           try
            { 
                if(r.next() && r.getInt(1)>0)
                {
                    
                    Books_issue b = new Books_issue(Integer.parseInt(tbid.getText()),Integer.parseInt(tsid.getText()));
                    q = "insert into Books_issue values ("+b.B_id+","+b.S_id+",'"+b.Issued_date+"','"+b.Expected_return+"',NULL,0)";
                    DB.insert(q);
                    q = "Update Books set B_av_copies = B_av_copies -1 where B_id = "+tbid.getText();
                    DB.insert(q);
                    this.dispose();
                    Main.open = true;
                    JOptionPane.showMessageDialog(null, "Book Issued Successfully.");
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "The Book is Unavailable");
                }

            }
            catch(SQLException ex){}        
        }
        else
        {
            JOptionPane.showMessageDialog(null, "!!PLEASE ENTER VALID DATA!!");
        }
    }

    @Override
    public void windowClosing(WindowEvent e) 
    {
        dispose();
        Main.open = true;
    }

     @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}

class Return_book extends JFrame implements ActionListener,WindowListener
{

    JLabel bid,sid;
    JTextField tbid,tsid;
    JButton submit;

    public Return_book()
    {
        setTitle("Return Book");
        addWindowListener(this);
        setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(2,0));
        bid = new JLabel("Enter Book ID : ",JLabel.LEFT);
        p1.add(bid);
        tbid = new JTextField(3);
        tbid.setEditable(true);
        p1.add(tbid);
        sid = new JLabel("Enter Student ID : ",JLabel.LEFT);
        p1.add(sid);
        tsid = new JTextField(3);
        tsid.setEditable(true);
        p1.add(tsid);
        add(p1);
        JPanel p2 = new JPanel();
        submit = new JButton("RETURN BOOK");
        submit.addActionListener(this);
        p2.add(submit);
        add("South",p2);
        setSize(800,150);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(!(tbid.getText().equals("") || tsid.getText().equals("") || Integer.parseInt(tbid.getText())<=0 || Integer.parseInt(tsid.getText())<=0))
        {
            String q = "Select Expected_return from Books_issue where B_id = "+tbid.getText()+" and S_id = "+tsid.getText();
            ResultSet r = DB.select(q);
            
            String msg;
           try
            { 
                if(r!=null && r.next())
                {
                    
                    Books_issue b = new Books_issue(Integer.parseInt(tbid.getText()),Integer.parseInt(tsid.getText()),r.getDate(1));
                    q = "update Books_issue set Return_date = '"+b.Return_date+"' where B_id = "+b.B_id+" and S_id = "+b.S_id;
                    DB.insert(q);
                    q = "Update Books set B_av_copies = B_av_copies +1 where B_id = "+tbid.getText();
                    DB.insert(q);
                    
                    if(b.Fine<=0)
                    {
                        msg = "Book Returned Successfully.";
                    }
                    else
                    {
                        q = "update Books_issue set Fine = '"+b.Fine+"' where B_id = "+b.B_id+" and S_id = "+b.S_id;
                        DB.insert(q);
                        msg = "Book Returned Successfully.\n Applicable Fine = Rs."+b.Fine+" .";
                    }

                    
                    this.dispose();
                    Main.open = true;
                    JOptionPane.showMessageDialog(null, msg);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "No Record Found.");
                }
            }
            catch(SQLException ex){}   
        }
        else
        {
            JOptionPane.showMessageDialog(null, "!!PLEASE ENTER VALID DATA!!");
        }
    }

     @Override
    public void windowClosing(WindowEvent e) 
    {
        dispose();
        Main.open = true;
    }

    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}

public class Index
{
    public static void main(String args[])
    {
        DB.setUp();
        new Main();    
    }
}