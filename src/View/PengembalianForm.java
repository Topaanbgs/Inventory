package View;

import Controller.DatabaseConnection;
import Model.Member;
import Model.TransaksiPengembalian;
import java.sql.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PengembalianForm extends javax.swing.JFrame {
    private final String memberID;
    private Connection conn;
    
    public PengembalianForm(Member member) {
        initComponents();
        this.memberID = String.valueOf(member.getMemberID());
        connectToDatabase();
    }
    
    private void connectToDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error koneksi ke database: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTransaction(String idTransaksi) {
        if (conn == null) {
            JOptionPane.showMessageDialog(this, 
                "Koneksi database belum terhubung.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String sql = """
            SELECT t.id_transaksi, m.name, t.tgl_peminjaman, t.tgl_pengembalian, i.nama_barang
            FROM transaksi t
            JOIN member m ON t.id_member = m.memberid
            JOIN inventory i ON t.id_barang = i.inventoryid
            WHERE t.id_transaksi = ? 
            AND t.id_member = ? 
            AND t.status = 'dipinjam'""";
            
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idTransaksi);
            ps.setString(2, this.memberID);
            
            try (ResultSet rs = ps.executeQuery()) {
                DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
                model.setRowCount(0);
                
                if (rs.next()) {
                    String namaMember = rs.getString("name");
                    String namaBarang = rs.getString("nama_barang");
                    Date tanggalPinjam = rs.getDate("tgl_peminjaman");
                    Date tanggalKembali = rs.getDate("tgl_pengembalian");
                    
                    model.addRow(new Object[]{
                        idTransaksi, 
                        namaMember, 
                        namaBarang,
                        tanggalPinjam, 
                        tanggalKembali
                    });
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Transaksi tidak ditemukan atau bukan milik Anda.", 
                        "Info", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            logError(e, "Error saat mencari transaksi");
        }
    }

    private void confirmReturn() {
    int selectedRow = jTable2.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, 
            "Pilih transaksi terlebih dahulu.", 
            "Warning", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    int idTransaksi = Integer.parseInt((String) jTable2.getValueAt(selectedRow, 0));
    
    try {
        conn.setAutoCommit(false);
        
        TransaksiPengembalian transaksi = new TransaksiPengembalian(idTransaksi);
        
        Date tglDikembalikan = new Date();

        String idBarang = transaksi.getId_barang();
        boolean success = transaksi.prosesKembaliParsial(idBarang, tglDikembalikan);
        
        if (success) {
            String updateBarang = "UPDATE inventory SET status = 'available' " +
                                "WHERE inventoryid = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBarang)) {
                ps.setString(1, transaksi.getId_barang());
                ps.executeUpdate();
            }
            
            conn.commit();
            
            if (transaksi.getTotalDenda() > 0) {
                String message = String.format("Barang berhasil dikembalikan.\n" +
                                             "Terdapat denda keterlambatan sebesar: Rp %.2f", 
                                             transaksi.getTotalDenda());
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Barang berhasil dikembalikan.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            InvoicePengembalianForm invoiceForm = new InvoicePengembalianForm();
            invoiceForm.setTransaksi(transaksi);
            invoiceForm.setVisible(true); 
            this.dispose();
            
            ((DefaultTableModel) jTable2.getModel()).setRowCount(0);
        } else {
            throw new SQLException("Gagal memproses pengembalian");
        }
        
    } catch (SQLException e) {
        try {
            conn.rollback();
        } catch (SQLException rollbackEx) {
            logError(rollbackEx, "Rollback gagal");
        }
        logError(e, "Error saat mengonfirmasi pengembalian");
    } finally {
        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            logError(e, "Error mengembalikan auto-commit");
        }
    }
}

    private void logError(Exception e, String message) {
        System.err.println(message);
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            message + ": " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Pengembalian Barang");

        jButton5.setText("Logout");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(238, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(113, 113, 113)
                .addComponent(jButton5)
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Transaksi", "Nama", "Nama Barang", "Tanggal Pengembalian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jButton4.setText("Konfirmasi");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel5.setText("ID Transaksi");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton6.setText("Cari");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Pengembalian Barang");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton7.setText("Peminjaman Barang");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
    String idTransaksi = jTextField1.getText();
        if (idTransaksi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan ID Transaksi terlebih dahulu", "Input Error", JOptionPane.WARNING_MESSAGE);
        } else {
            searchTransaction(idTransaksi);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    confirmReturn();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
    Member currentMember = Member.getLoggedInMember();
        new PeminjamanForm(currentMember).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        new LoginForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PengembalianForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengembalianForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengembalianForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengembalianForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
            java.awt.EventQueue.invokeLater(() -> {
        Member member = Member.getLoggedInMember();
        new PengembalianForm(member).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
