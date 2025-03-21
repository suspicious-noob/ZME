package com.zme;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.awt.Font;
import java.awt.Image;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MainApp extends JFrame {
	private static final long serialVersionUID = 1L; // Fixes the warning
	private JTextField sourceTextField;
	private JTextField destinationTextField;
	private JButton destinationBrowsebtn;
	private JButton extractBtn;
	private JTextArea logsTextarea;
	private JProgressBar progressBar;
	private JRadioButton moveRbtn;
	private JRadioButton copyRbtn;
	private JButton cancelBtn;
	private volatile boolean isCancelled = false;
	private JScrollPane scrollPane;
	private SwingWorker<Void, String> worker;
	private JLabel lblNewLabel;

	public MainApp() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/resources/ZME-logo-cropped.png")));
        setTitle("Zomboid Mod Extractor [v1.2.8]");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setResizable(false);
        getContentPane().setLayout(null);
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBounds(10, 183, 514, 23);
        getContentPane().add(progressBar);
        
        sourceTextField = new JTextField();
        sourceTextField.setEditable(false);
        sourceTextField.setToolTipText("Source directory");
        sourceTextField.setBounds(10, 60, 402, 20);
        getContentPane().add(sourceTextField);
        sourceTextField.setColumns(10);
        
        JButton sourceBrowsebtn = new JButton("Browse...");
        sourceBrowsebtn.addActionListener(e -> selectDirectory(sourceTextField));
        sourceBrowsebtn.setBounds(422, 59, 102, 23);
        getContentPane().add(sourceBrowsebtn);
        
        destinationTextField = new JTextField();
        destinationTextField.setEditable(false);
        destinationTextField.setBounds(10, 116, 402, 20);
        getContentPane().add(destinationTextField);
        destinationTextField.setColumns(10);
        
        destinationBrowsebtn = new JButton("Browse...");
        destinationBrowsebtn.addActionListener(e -> selectDirectory(destinationTextField));
        destinationBrowsebtn.setBounds(422, 115, 102, 23);
        getContentPane().add(destinationBrowsebtn);
        
        JLabel deslbl = new JLabel("Destination:");
        deslbl.setBounds(10, 91, 77, 14);
        getContentPane().add(deslbl);
        
        JLabel srclbl = new JLabel("Source:");
        srclbl.setBounds(10, 35, 46, 14);
        getContentPane().add(srclbl);
        
        extractBtn = new JButton("Extract");
        extractBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
        extractBtn.addActionListener(e -> startExtraction());
        extractBtn.setBounds(211, 149, 102, 23);
        getContentPane().add(extractBtn);
        
        
        moveRbtn = new JRadioButton("Move");
        moveRbtn.setBounds(10, 149, 58, 23);
        getContentPane().add(moveRbtn);
        
        copyRbtn = new JRadioButton("Copy");
        copyRbtn.setSelected(true);
        copyRbtn.setBounds(70, 149, 109, 23);
        getContentPane().add(copyRbtn);
        
        cancelBtn = new JButton("Cancel");
        cancelBtn.setEnabled(false);
        cancelBtn.setBounds(323, 149, 89, 23);
        cancelBtn.addActionListener(e -> cancelExtraction());
        getContentPane().add(cancelBtn);
        
        destinationTextField.setEnabled(false); // Initially disabled
        destinationBrowsebtn.setEnabled(false); // Initially disabled
        extractBtn.setEnabled(false); // Initially disabled
        cancelBtn.setEnabled(false);
        
        
        ButtonGroup group = new ButtonGroup();
        group.add(moveRbtn);
        group.add(copyRbtn);
        
        
        
        scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 217, 514, 133);
        getContentPane().add(scrollPane);
        
        logsTextarea = new JTextArea();
        scrollPane.setViewportView(logsTextarea);
        
        lblNewLabel = new JLabel("");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setVerticalAlignment(SwingConstants.CENTER);
        ImageIcon originalIcon = new ImageIcon(MainApp.class.getResource("/resources/ZME-logo-cropped.png"));
        Image img = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        lblNewLabel.setIcon(new ImageIcon(img));
        lblNewLabel.setBounds(422, 11, 102, 51);
        getContentPane().add(lblNewLabel);
        
        lblNewLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDeveloperInfo();
            }
        });
    }
	
	private void showDeveloperInfo() {
	    JDialog aboutDialog = new JDialog(this, "About", true);
	    aboutDialog.setSize(300, 250); // Increased height to fit the logo
	    aboutDialog.getContentPane().setLayout(null);
	    aboutDialog.setLocationRelativeTo(this);
	    aboutDialog.setResizable(false);

	    // Logo
	    JLabel logoLabel = new JLabel("");
	    logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        ImageIcon originalIcon = new ImageIcon(MainApp.class.getResource("/resources/ZME-logo-cropped.png"));
        Image img = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(img));
	    logoLabel.setBounds(75, 10, 150, 150); // Adjust position & size
	    aboutDialog.getContentPane().add(logoLabel);

	    // Developer Info
	    JLabel infoLabel = new JLabel("<html><center><b>Zomboid Mod Extractor</b><br>Version 1.2.8<br>Developed by: Suspicious Noob</center></html>", SwingConstants.CENTER);
	    infoLabel.setBounds(20, 130, 260, 60);
	    aboutDialog.getContentPane().add(infoLabel);

	   

	    aboutDialog.setVisible(true);
	}
	
	 private void selectDirectory(JTextField textField) {
	        JFileChooser fileChooser = new JFileChooser();
	        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	        fileChooser.setDialogTitle("Select Directory");

	        int result = fileChooser.showOpenDialog(this);
	        if (result == JFileChooser.APPROVE_OPTION) {
	        	File selectedDir = fileChooser.getSelectedFile();
	        	String selectedPath = selectedDir.getAbsolutePath();
	            //logMessage("Selected directory: " + selectedDir.getAbsolutePath());
	            
	            if (textField == destinationTextField && selectedPath.equals(sourceTextField.getText())) {
	                JOptionPane.showMessageDialog(this, "Destination cannot be the same as the source!", "Error", JOptionPane.ERROR_MESSAGE);
	                destinationTextField.setText(""); // Clear invalid selection
	                extractBtn.setEnabled(false); //Disable button if enabled
	                return;
	            }
	            
	            textField.setText(selectedPath);
	            if(textField == destinationTextField) {
	            	logMessage("Selected Destination directory: " + selectedPath);
	            }else {
	            	logMessage("Selected Source directory: " + selectedPath);
	            }
	            

	            // Enable destination fields if source is selected
	            if (textField == sourceTextField) {
	                destinationTextField.setEnabled(true);
	                destinationBrowsebtn.setEnabled(true);
	            }

	            // Enable Extract button only when both directories are selected
	            extractBtn.setEnabled(!sourceTextField.getText().isEmpty() && !destinationTextField.getText().isEmpty());
	        }
	    }
	
	private void startExtraction() {
		
		// Ensure directories are selected
	    String sourceDir = sourceTextField.getText();
	    String destinationDir = destinationTextField.getText();
	    
	    if (sourceDir.isEmpty() || destinationDir.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Please select both source and destination directories!", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    
	    File srcFolder = new File(sourceDir);
	    File destFolder = new File(destinationDir);
	    
	    if (!srcFolder.exists() || !srcFolder.isDirectory()) {
	        JOptionPane.showMessageDialog(this, "Invalid source directory!", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    
	    if (!destFolder.exists()) {
	        destFolder.mkdirs();  // Create destination directory if it doesn't exist
	    }
	    
        logMessage("Starting extraction...");
        progressBar.setValue(0);
        isCancelled = false; // Reset cancellation flag
        extractBtn.setEnabled(false);
        cancelBtn.setEnabled(true);
        
        worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                File[] folders = srcFolder.listFiles(File::isDirectory);
                if (folders == null || folders.length == 0) {
                    publish("No folders found in the source directory.");
                    return null;
                }

                int progress = 0;
                int totalFolders = folders.length;

                for (int i = 0; i < totalFolders; i++) {
                	if (isCancelled) {
                        publish("[CANCELLED] Extraction process was cancelled1.");
                        progressBar.setValue(0);
                        return null;
                    }
                	
                    File folder1 = folders[i];

                    // Skip destination directory if it's inside the source
                    if (folder1.getAbsolutePath().equals(destFolder.getAbsolutePath())) {
                        publish("[SKIP] Skipping extraction folder inside source.");
                        continue;
                    }

                    File[] subFolders = folder1.listFiles(File::isDirectory);
                    if (subFolders == null || subFolders.length != 1) {
                        publish("[WARNING] Skipping " + folder1.getName() + " (invalid structure)");
                        continue;
                    }

                    File actualFolderContainer = subFolders[0]; // This contains the actual mod folders

                    File[] actualModFolders = actualFolderContainer.listFiles(File::isDirectory);
                    if (actualModFolders == null || actualModFolders.length == 0) {
                        publish("[WARNING] No valid mod folders in " + actualFolderContainer.getName());
                        continue;
                    }

                    for (File modFolder : actualModFolders) {
                    	if (isCancelled) {
                            publish("[CANCELLED] Extraction process was cancelled3.");
                            progressBar.setValue(0);
                            return null;
                        }
                        File target = new File(destFolder, modFolder.getName());

                        if (moveRbtn.isSelected()) {
                            try {
                                Files.move(modFolder.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                publish("[MOVE] Moved " + modFolder.getName() + " to " + destinationDir);
                            } catch (Exception e) {
                                publish("[ERROR] Failed to move " + modFolder.getName() + ": " + e.getMessage());
                            }
                        } else {
                            // Copy operation
                            if (copyDirectory(modFolder, target)) {
                                publish("[COPY] Copied " + modFolder.getName() + " to " + destinationDir);
                            } else {
                                publish("[ERROR] Failed to copy " + modFolder.getName());
                            }
                        }
                    }

                    progress = (int) (((i + 1) / (double) totalFolders) * 100);
                    setProgress(progress);
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    logMessage(message);
                }
            }

            @Override
            protected void done() {
                if (isCancelled) {
                    logMessage("[CANCELLED] Extraction was stopped by user.");
                    progressBar.setValue(0); // Clear progress bar on cancel
                } else {
                    logMessage("Extraction process complete!");
                    progressBar.setValue(100);
                }
                extractBtn.setEnabled(true);
                cancelBtn.setEnabled(false);
            }
        };

        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer) evt.getNewValue());
            }
        });

        worker.execute();
        
//        boolean isMoveSelected = moveRbtn.isSelected(); // Check which option is selected
//        logMessage("Operation Mode: " + (isMoveSelected ? "Move" : "Copy"));
//
//        // Simulate progress
//        new Timer(500, new ActionListener() {
//            int progress = 0;
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (progress < 100) {
//                    progress += 20;
//                    progressBar.setValue(progress);
//                    logMessage("Extracting... " + progress + "%");
//                } else {
//                	((Timer) e.getSource()).stop();
//                    logMessage(isMoveSelected ? "Move operation complete!" : "Copy operation complete!");
//                }
//            }
//        }).start();
        
    }
	
	private void cancelExtraction() {
	    if (worker != null && !worker.isDone()) {
	        isCancelled = true;
	        worker.cancel(true);
	        logMessage("[CANCEL] Extraction process was cancelled.");
	        
	        progressBar.setValue(0);  // Reset progress bar
	        extractBtn.setEnabled(true);
	        cancelBtn.setEnabled(false);
	        destinationBrowsebtn.setEnabled(true); // Enable back destination selection
	    }
	}
	
	private boolean copyDirectory(File source, File destination) {
	    if (!destination.exists()) {
	        destination.mkdirs();
	    }

	    File[] files = source.listFiles();
	    if (files == null) return false;

	    for (File file : files) {
	        File newFile = new File(destination, file.getName());
	        try {
	            if (file.isDirectory()) {
	                copyDirectory(file, newFile);
	            } else {
	                java.nio.file.Files.copy(file.toPath(), newFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
	            }
	        } catch (Exception e) {
	            logMessage("[ERROR] " + e.getMessage());
	            return false;
	        }
	    }
	    return true;
	}
	
	private void logMessage(String message) {
	    SwingUtilities.invokeLater(() -> {
	        logsTextarea.append(message + "\n");
	        logsTextarea.setCaretPosition(logsTextarea.getDocument().getLength());
	    });
	}
	

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp frame = new MainApp();
            frame.setVisible(true);
        });
    }
}
