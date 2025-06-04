package presentacion;

import datos.ConexionDB;
import modelo.Producto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class SistemaProductos extends JInternalFrame {

    private JTextField txtNombre, txtCodigo, txtPrecio, txtStock;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnLimpiar;
    private int idSeleccionado = -1;

    public SistemaProductos() {
        configurarInterfaz();
        cargarProductos();
    }

    private void configurarInterfaz() {
        setTitle("Gestión de Productos");
        setSize(1000, 600);
        //setLocationRelativeTo(null);
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
        aplicarTemaOscuro();

        JPanel contenedorPrincipal = new JPanel(new BorderLayout());
        contenedorPrincipal.setBackground(Color.WHITE);

        contenedorPrincipal.add(crearPanelFormulario(), BorderLayout.NORTH);
        contenedorPrincipal.add(crearPanelTabla(), BorderLayout.CENTER);

        setContentPane(contenedorPrincipal);
        configurarEventos();
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Producto"));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = crearCampoTexto();
        txtCodigo = crearCampoTexto();
        txtPrecio = crearCampoTexto();
        txtStock = crearCampoTexto();

        agregarComponente(panel, new JLabel("Nombre:"), 0, 0, gbc);
        agregarComponente(panel, txtNombre, 1, 0, gbc);
        agregarComponente(panel, new JLabel("Código:"), 0, 1, gbc);
        agregarComponente(panel, txtCodigo, 1, 1, gbc);
        agregarComponente(panel, new JLabel("Precio:"), 0, 2, gbc);
        agregarComponente(panel, txtPrecio, 1, 2, gbc);
        agregarComponente(panel, new JLabel("Stock:"), 0, 3, gbc);
        agregarComponente(panel, txtStock, 1, 3, gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(crearPanelBotones(), gbc);

        return panel;
    }

    private JPanel crearPanelBotones() {
        btnRegistrar = crearBoton("Registrar", new Color(76, 175, 80));
        btnActualizar = crearBoton("Actualizar", new Color(33, 150, 243));
        btnEliminar = crearBoton("Eliminar", new Color(244, 67, 54));
        btnLimpiar = crearBoton("Limpiar", new Color(158, 158, 158));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        panel.add(btnRegistrar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Listado de Productos"));

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Código", "Precio", "Stock"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setBackground(Color.WHITE);
        tablaProductos.setForeground(Color.BLACK);
        tablaProductos.setGridColor(Color.GRAY);

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private void configurarEventos() {
        btnRegistrar.addActionListener(e -> registrarProducto());
        btnActualizar.addActionListener(e -> actualizarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tablaProductos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fila = tablaProductos.getSelectedRow();
                if (fila >= 0) cargarDatosFormulario(fila);
            }
        });

        txtNombre.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && !Character.isWhitespace(c)) e.consume();
            }
        });

        txtCodigo.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetterOrDigit(c)) e.consume();
            }
        });

        txtPrecio.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String texto = txtPrecio.getText();
                if (!Character.isDigit(c) && c != '.' || (c == '.' && texto.contains("."))) e.consume();
            }
        });

        txtStock.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) e.consume();
            }
        });
    }

    private void registrarProducto() {
        try {
            validarCampos();
            Producto p = crearProductoDesdeFormulario();

            String sql = "INSERT INTO producto (nombre, codigo, precio, stock) VALUES (?, ?, ?, ?)";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, p.getNombre());
                ps.setString(2, p.getCodigo());
                ps.setFloat(3, p.getPrecio());
                ps.setInt(4, p.getStock());
                ps.executeUpdate();

                cargarProductos();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Producto registrado exitosamente!");
            }

        } catch (IllegalArgumentException | SQLException ex) {
            manejarError(ex.getMessage());
        }
    }

    private void actualizarProducto() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto");
            return;
        }

        try {
            validarCampos();
            Producto p = crearProductoDesdeFormulario();

            String sql = "UPDATE producto SET nombre = ?, codigo = ?, precio = ?, stock = ? WHERE id = ?";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, p.getNombre());
                ps.setString(2, p.getCodigo());
                ps.setFloat(3, p.getPrecio());
                ps.setInt(4, p.getStock());
                ps.setInt(5, idSeleccionado);
                ps.executeUpdate();

                cargarProductos();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente!");
            }

        } catch (IllegalArgumentException | SQLException ex) {
            manejarError(ex.getMessage());
        }
    }

    private void eliminarProducto() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Eliminar producto?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM producto WHERE id = ?";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idSeleccionado);
                ps.executeUpdate();

                cargarProductos();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Producto eliminado!");

            } catch (SQLException ex) {
                manejarError("Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT * FROM producto";

        try (Connection conn = ConexionDB.AbrirConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("codigo"),
                        rs.getFloat("precio"),
                        rs.getInt("stock")
                });
            }

        } catch (SQLException ex) {
            manejarError("Error al cargar datos: " + ex.getMessage());
        }
    }

    private Producto crearProductoDesdeFormulario() {
        return new Producto(
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                Float.parseFloat(txtPrecio.getText().trim()),
                Integer.parseInt(txtStock.getText().trim())
        );
    }

    private void validarCampos() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtCodigo.getText().trim().isEmpty() ||
                txtPrecio.getText().trim().isEmpty() ||
                txtStock.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }

        float precio = Float.parseFloat(txtPrecio.getText().trim());
        int stock = Integer.parseInt(txtStock.getText().trim());

        if (precio <= 0) throw new IllegalArgumentException("Precio debe ser mayor a 0");
        if (stock < 0) throw new IllegalArgumentException("Stock no puede ser negativo");
    }

    private void cargarDatosFormulario(int fila) {
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtCodigo.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtPrecio.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtStock.setText(modeloTabla.getValueAt(fila, 4).toString());
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtCodigo.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        idSeleccionado = -1;
    }

    private void manejarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void aplicarTemaOscuro() {
        UIManager.put("Panel.background", new Color(34, 40, 49));
        UIManager.put("Label.foreground", Color.BLACK);
        UIManager.put("TextField.background", new Color(57, 62, 70));
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("Table.background", new Color(57, 62, 70));
        UIManager.put("Table.foreground", Color.WHITE);
        UIManager.put("Button.background", new Color(0, 173, 181));
        UIManager.put("Button.foreground", Color.WHITE);
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField(20);
        campo.setPreferredSize(new Dimension(250, 30));
        return campo;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(120, 35));
        return boton;
    }

    private void agregarComponente(JPanel panel, Component comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(comp, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaProductos().setVisible(true));
    }

}
