package view;

import controller.OtpController;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class RegisterView extends JFrame {


    private OtpController OtpController;
    
    private JButton btnBack;
    private JProgressBar progressBar;
    private Timer progressTimer;
    private JDialog loadingDialog;
    private int progressValue = 0;

    public RegisterView() {
  
        OtpController = new OtpController();
        
        setTitle("Registrasi Management");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Latar Belakang dengan gradient hijau
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(34, 139, 34), // Forest Green
                    0, getHeight(), new Color(144, 238, 144) // Light Green
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Panel untuk form dengan background semi-transparan
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        formPanel.setOpaque(false);

        // Title Panel
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("REGISTER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        titleLabel.setForeground(Color.BLACK);
        
        JLabel subtitleLabel = new JLabel("E-Waste Management");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        subtitleLabel.setForeground(Color.BLACK);

        // Input data
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel labelEmail = new JLabel("Email:");
        JTextField fieldEmail = new JTextField(20);

        JLabel labelPassword = new JLabel("Password:");
        JPasswordField fieldPassword = new JPasswordField(20);

//        JLabel labelKTP = new JLabel("Nomor KTP:");
//        JTextField fieldKTP = new JTextField(20);
//
//        JLabel labelKK = new JLabel("Nomor KK:");
//        JTextField fieldKK = new JTextField(20);

        JButton btnRegister = new JButton("Daftar");
        btnRegister.setBackground(new Color(70, 130, 180));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));

        // Action listener untuk tombol registrasi
        btnRegister.addActionListener(e -> {
            String email = fieldEmail.getText().trim();
            String password = new String(fieldPassword.getPassword()).trim();
           
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            showLoadingDialog();
            progressTimer.start();
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                private String otp;
                private Exception exception;
                
                @Override
                protected Boolean doInBackground() throws Exception {
                    try {
                        // Tunggu sampai progress mencapai sekitar 60% sebelum benar-benar mengirim OTP
                        while (progressValue < 60) {
                            Thread.sleep(50);
                        }
                        otp = OtpController.generateOTP();
                        return OtpController.sendOtpRegister(email, otp);
                    } catch (Exception ex) {
                        exception = ex;
                        return false;
                    }
                }
                
                @Override
                protected void done() {
                    // Tunggu sampai progress bar mencapai 100%
                    while (progressValue < 100) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    
                    Timer closeTimer = new Timer(500, evt -> {
                        ((Timer)evt.getSource()).stop();
                        loadingDialog.dispose();
                        
                        try {
                            boolean success = get();
                            if (success) {
                                new RegisterOtpVerificationView(email, password, otp, RegisterView.this).setVisible(true);
                                RegisterView.this.setVisible(false);
                            } else {
                                if (exception != null) {
                                    JOptionPane.showMessageDialog(RegisterView.this, 
                                        "Terjadi kesalahan: " + exception.getMessage(), 
                                        "Kesalahan", 
                                        JOptionPane.ERROR_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(RegisterView.this, 
                                        "Gagal mengirim OTP", 
                                        "Kesalahan", 
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(RegisterView.this, 
                                "Terjadi kesalahan: " + ex.getMessage(), 
                                "Kesalahan", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    closeTimer.setRepeats(false);
                    closeTimer.start();
                }
            };
            
            worker.execute();
            loadingDialog.setVisible(true);
        });

        // Tambahkan tombol kembali dengan GridBagConstraints
        btnBack = new JButton("Kembali");
        btnBack.setBackground(new Color(70, 130, 180));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Buat panel khusus untuk tombol back di mainPanel
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setOpaque(false);
        backPanel.add(btnBack);
        
        // Atur layout untuk mainPanel
        GridBagConstraints backGbc = new GridBagConstraints();
        backGbc.gridx = 0;
        backGbc.gridy = 0;
        backGbc.anchor = GridBagConstraints.NORTHWEST;
        backGbc.fill = GridBagConstraints.HORIZONTAL;
        backGbc.insets = new Insets(10, 10, 0, 10);
        mainPanel.add(backPanel, backGbc);
        
        // Tambahkan formPanel dengan constraint baru
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formGbc.weightx = 1.0;
        formGbc.weighty = 1.0;
        formGbc.fill = GridBagConstraints.BOTH;
        formGbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(formPanel, formGbc);
        
        btnBack.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });

        // Add title components to titlePanel
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridx = 0;
        titleGbc.gridy = 0;
        titleGbc.insets = new Insets(0, 0, 5, 0);
        titlePanel.add(titleLabel, titleGbc);

        titleGbc.gridy = 1;
        titleGbc.insets = new Insets(0, 0, 20, 0);
        titlePanel.add(subtitleLabel, titleGbc);

        // Add components to mainPanel using GridBagConstraints
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.insets = new Insets(20, 20, 20, 20);
        
        // Add formPanel to mainPanel
        mainPanel.add(formPanel, mainGbc);

        // Remove these BorderLayout adds
        // mainPanel.add(titlePanel, BorderLayout.NORTH);
        // mainPanel.add(formPanel, BorderLayout.CENTER);

        // Add titlePanel to formPanel at the top
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 30, 10);
        formPanel.add(titlePanel, gbc);

        // Email field
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.add(labelEmail, gbc);

        gbc.gridx = 1;
        formPanel.add(fieldEmail, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(labelPassword, gbc);

        gbc.gridx = 1;
        formPanel.add(fieldPassword, gbc);

        // Register button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(btnRegister, gbc);

        add(mainPanel);
    }

    private void showLoadingDialog() {
        loadingDialog = new JDialog(this, "Loading", true);
        loadingDialog.setLayout(new BorderLayout());
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(250, 20));
        progressBar.setStringPainted(true);
        progressBar.setBorderPainted(true);
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(new Color(34, 139, 34));
        progressValue = 0;
        progressBar.setValue(progressValue);
        progressBar.setString(progressValue + "%");
        
        JLabel statusLabel = new JLabel("Mengirim OTP, mohon tunggu...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(statusLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        
        progressTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressValue += 1;
                if (progressValue <= 100) {
                    progressBar.setValue(progressValue);
                    progressBar.setString(progressValue + "%");
                    
                    if (progressValue < 30) {
                        statusLabel.setText("Mempersiapkan pengiriman OTP...");
                    } else if (progressValue < 60) {
                        statusLabel.setText("Menghasilkan kode OTP...");
                    } else if (progressValue < 90) {
                        statusLabel.setText("Mengirim OTP ke email...");
                    } else {
                        statusLabel.setText("Hampir selesai...");
                    }
                } else {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        
        loadingDialog.add(panel);
        loadingDialog.setResizable(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterView().setVisible(true));
    }
}
