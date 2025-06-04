/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package presentacion;



import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.DetalleFactura;
import modelo.Factura;
import modelo.Persona;
import modelo.Producto;
import negocio.FacturaServicio;
import negocio.PersonaServicio;
import negocio.ProductoServicio;
/**
 *
 * @author diego
 */
public class SistemaFacturas extends JInternalFrame {
private List<DetalleFactura> detallesFactura = new ArrayList<>();
    private Persona personaEncontrada;
    private final FacturaServicio facturaServicio;
    private final PersonaServicio personaServicio;
    private final ProductoServicio productoServicio;
    private Producto productoEncontrado;
    private DefaultTableModel modeloTabla;

    public SistemaFacturas() {
        facturaServicio = new FacturaServicio();
        personaServicio = new PersonaServicio();
        productoServicio = new ProductoServicio();
        initComponents();
        configurarTabla();
        personalizarInterfaz();
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

    }

    private void personalizarInterfaz() {
        // Configurar filtros numéricos
        txtCedula1.setDocument(new NumericFilter());
        txtCantidad.setDocument(new NumericFilter());
        
        // Configurar modelo de tabla
        configurarTabla();
    }
    

    // Clase para filtro que solo permite números
    class NumericFilter extends javax.swing.text.PlainDocument {
        @Override
        public void insertString(int offs, String str, javax.swing.text.AttributeSet a) 
            throws javax.swing.text.BadLocationException {
            if (str == null) {
                return;
            }
            String newStr = str.replaceAll("[^0-9]", "");
            super.insertString(offs, newStr, a);
        }
    }

    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[]{"Producto", "Código", "Precio Unitario", "Cantidad", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProductos.setModel(modeloTabla);
        tblProductos.setRowHeight(25);
        tblProductos.getTableHeader().setFont(new java.awt.Font("Segoe UI", 1, 12));
    }

    private void BuscarPersona() {
        String cedula = txtCedula1.getText().trim();
        if(cedula.isEmpty()) {
            mostrarMensaje("Ingrese un número de cédula", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.personaEncontrada = this.personaServicio.BuscarPersonaPorCedula(cedula);
        if(this.personaEncontrada != null) {
            actualizarInformacionCliente();
            mostrarMensaje("Cliente encontrado: " + this.personaEncontrada.getNombre(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            limpiarInformacionCliente();
            mostrarMensaje("No se encontró un cliente con esa cédula", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarInformacionCliente() {
        lblNombre1.setText("Nombre: " + this.personaEncontrada.getNombre() + " " + this.personaEncontrada.getApellido());
        lblCedulaCliente1.setText("Cédula: " + this.personaEncontrada.getCedula());
        lblCorreo1.setText("Correo: " + this.personaEncontrada.getCorreo());
    }

    private void limpiarInformacionCliente() {
        lblNombre1.setText("Nombre: ");
        lblCedulaCliente1.setText("Cédula: ");
        lblCorreo1.setText("Correo: ");
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    private void BuscarProducto() {
        String codigo = txtCodigoProducto.getText().trim();
        if(codigo.isEmpty()) {
            mostrarMensaje("Ingrese un código de producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            lblProducto.setText("Producto: ");
            return;
        }

        this.productoEncontrado = this.productoServicio.buscarProductoPorCodigo(codigo);
        if(this.productoEncontrado != null) {
            txtPrecio.setText(String.format("%.2f", this.productoEncontrado.getPrecio()));
            lblProducto.setText("Producto: " + productoEncontrado.getNombre());
            mostrarMensaje("Producto encontrado: " + productoEncontrado.getNombre(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            lblProducto.setText("Producto: ");
            mostrarMensaje("No se encontró un producto con ese código", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }   

    private void AgregarProducto() {
        if(productoEncontrado == null) {
            mostrarMensaje("Primero busque un producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if(cantidad <= 0) {
                mostrarMensaje("La cantidad debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(cantidad > productoEncontrado.getStock()) {
                mostrarMensaje("No hay suficiente stock. Stock disponible: " + productoEncontrado.getStock(), 
                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si el producto ya existe en la factura
            boolean productoExistente = false;
            for(DetalleFactura detalle : detallesFactura) {
                if(detalle.getProducto().getCodigo().equals(productoEncontrado.getCodigo())) {
                    detalle.setCantidad(detalle.getCantidad() + cantidad);

                    if(detalle.getCantidad() > productoEncontrado.getStock()) {
                        mostrarMensaje("No hay suficiente stock. Stock disponible: " + productoEncontrado.getStock(), 
                                      "Error", JOptionPane.ERROR_MESSAGE);
                        detalle.setCantidad(detalle.getCantidad() - cantidad);
                        return;
                    }

                    productoExistente = true;
                    break;
                }
            }

            if(!productoExistente) {
                DetalleFactura nuevoDetalle = new DetalleFactura(
                    cantidad, productoEncontrado.getPrecio(), productoEncontrado);
                detallesFactura.add(nuevoDetalle);
            }

            actualizarTablaDetalles();
            limpiarCamposProducto();

        } catch(NumberFormatException ex) {
            mostrarMensaje("La cantidad debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTablaDetalles() {
        modeloTabla.setRowCount(0);

        for(DetalleFactura detalle : detallesFactura) {
            Object[] fila = {
                detalle.getProducto().getNombre(),
                detalle.getProducto().getCodigo(),
                String.format("%.2f", detalle.getPrecioUnitario()),
                detalle.getCantidad(),
                String.format("%.2f", detalle.getPrecioUnitario() * detalle.getCantidad())
            };
            modeloTabla.addRow(fila);
        }

        // Calcular subtotal, IVA y total
        double subtotal = detallesFactura.stream()
            .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad())
            .sum();

        double iva = subtotal * 0.15;
        double total = subtotal + iva;

        lblSubtotal.setText(String.format("Subtotal: $%.2f", subtotal));
        lblIVA.setText(String.format("IVA (15%%): $%.2f", iva));
        lblTotal.setText(String.format("Total: $%.2f", total));
    }

    private void limpiarCamposProducto() {
        txtCodigoProducto.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        lblProducto.setText("Producto: ");
        productoEncontrado = null;
    }

    private void RegistrarFactura() {
        if(validarFactura()) {
            int confirmacion = JOptionPane.showConfirmDialog(
                this, 
                "¿Está seguro de crear esta factura?", 
                "Confirmar Factura", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                Factura nuevaFactura = new Factura(personaEncontrada, detallesFactura);
                facturaServicio.RegistrarNuevaFactura(nuevaFactura);
                mostrarMensaje("Factura creada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
            }
        }
    }

    private boolean validarFactura() {
        if(personaEncontrada == null) {
            mostrarMensaje("Primero busque un cliente", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if(detallesFactura.isEmpty()) {
            mostrarMensaje("Agregue al menos un producto a la factura", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        detallesFactura.clear();
        personaEncontrada = null;
        modeloTabla.setRowCount(0);
        txtCedula1.setText("");
        limpiarInformacionCliente();
        limpiarCamposProducto();
        lblSubtotal.setText("Subtotal: $0.00");
        lblIVA.setText("IVA (15%): $0.00");
        lblTotal.setText("Total: $0.00");
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlSuperior = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        lblSubtotal = new javax.swing.JLabel();
        lblIVA = new javax.swing.JLabel();
        pnlSuperior1 = new javax.swing.JPanel();
        lblCodigoproducto = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        lblCantidad = new javax.swing.JLabel();
        lblPrecio = new javax.swing.JLabel();
        lblProducto = new javax.swing.JLabel();
        txtCodigoProducto = new javax.swing.JTextField();
        btnBuscarProducto = new javax.swing.JButton();
        txtPrecio = new javax.swing.JTextField();
        btnBuscarCliente2 = new javax.swing.JButton();
        btnAgregarFactura = new javax.swing.JButton();
        pnlCentral = new javax.swing.JPanel();
        btnBuscarCliente3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        pnlSuperior2 = new javax.swing.JPanel();
        lblCedula1 = new javax.swing.JLabel();
        txtCedula1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        lblNombre1 = new javax.swing.JLabel();
        lblCedulaCliente1 = new javax.swing.JLabel();
        lblCorreo1 = new javax.swing.JLabel();
        btnBuscarCliente = new javax.swing.JButton();
        btnGenerarFactura = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlSuperior.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51), 2), "Totales", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(255, 0, 51))); // NOI18N

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTotal.setText("Total: $0.00");

        lblSubtotal.setText("Subtotal: $0.00");

        lblIVA.setText("IVA (15%): $0.00");

        javax.swing.GroupLayout pnlSuperiorLayout = new javax.swing.GroupLayout(pnlSuperior);
        pnlSuperior.setLayout(pnlSuperiorLayout);
        pnlSuperiorLayout.setHorizontalGroup(
            pnlSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSuperiorLayout.createSequentialGroup()
                .addContainerGap(375, Short.MAX_VALUE)
                .addGroup(pnlSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblIVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSubtotal)
                    .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(34, 34, 34))
        );
        pnlSuperiorLayout.setVerticalGroup(
            pnlSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSuperiorLayout.createSequentialGroup()
                .addComponent(lblSubtotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblIVA)
                .addGap(18, 18, 18)
                .addComponent(lblTotal)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 369;
        gridBagConstraints.ipady = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 17, 52, 0);
        getContentPane().add(pnlSuperior, gridBagConstraints);

        pnlSuperior1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 204, 0), 2, true), "Datos del Prodcuto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(0, 204, 0))); // NOI18N

        lblCodigoproducto.setText("Codigo del Producto:");

        lblCantidad.setText("Cantidad:");

        lblPrecio.setText("Precio Unitrario:");

        lblProducto.setText("Producto:");

        btnBuscarProducto.setBackground(new java.awt.Color(0, 204, 0));
        btnBuscarProducto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBuscarProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscarProducto.setText("Buscar Producto");
        btnBuscarProducto.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProductoActionPerformed(evt);
            }
        });

        btnBuscarCliente2.setBackground(new java.awt.Color(0, 204, 0));
        btnBuscarCliente2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBuscarCliente2.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscarCliente2.setText("Buscar Cliente");
        btnBuscarCliente2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnBuscarCliente2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarCliente2ActionPerformed(evt);
            }
        });

        btnAgregarFactura.setBackground(new java.awt.Color(0, 204, 0));
        btnAgregarFactura.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAgregarFactura.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarFactura.setText("Agregar a Factura");
        btnAgregarFactura.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnAgregarFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarFacturaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSuperior1Layout = new javax.swing.GroupLayout(pnlSuperior1);
        pnlSuperior1.setLayout(pnlSuperior1Layout);
        pnlSuperior1Layout.setHorizontalGroup(
            pnlSuperior1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSuperior1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSuperior1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSuperior1Layout.createSequentialGroup()
                        .addGroup(pnlSuperior1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlSuperior1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtCantidad, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                                .addComponent(txtCodigoProducto, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(lblCodigoproducto)
                            .addComponent(lblCantidad))
                        .addGap(31, 31, 31)
                        .addGroup(pnlSuperior1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblPrecio)
                            .addComponent(txtPrecio)
                            .addComponent(btnBuscarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)))
                    .addGroup(pnlSuperior1Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(btnAgregarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlSuperior1Layout.createSequentialGroup()
                        .addGap(135, 135, 135)
                        .addComponent(lblProducto)))
                .addContainerGap(54, Short.MAX_VALUE))
            .addGroup(pnlSuperior1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSuperior1Layout.createSequentialGroup()
                    .addContainerGap(239, Short.MAX_VALUE)
                    .addComponent(btnBuscarCliente2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(25, 25, 25)))
        );
        pnlSuperior1Layout.setVerticalGroup(
            pnlSuperior1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSuperior1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCodigoproducto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSuperior1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                .addGap(9, 9, 9)
                .addGroup(pnlSuperior1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCantidad)
                    .addComponent(lblPrecio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSuperior1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAgregarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblProducto)
                .addGap(13, 13, 13))
            .addGroup(pnlSuperior1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlSuperior1Layout.createSequentialGroup()
                    .addGap(60, 60, 60)
                    .addComponent(btnBuscarCliente2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(127, Short.MAX_VALUE)))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 48;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 32, 0, 35);
        getContentPane().add(pnlSuperior1, gridBagConstraints);

        pnlCentral.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 0, 153), 2, true), "Detalle Factura", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(102, 0, 153))); // NOI18N

        btnBuscarCliente3.setBackground(new java.awt.Color(0, 204, 0));
        btnBuscarCliente3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBuscarCliente3.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscarCliente3.setText("Buscar Cliente");
        btnBuscarCliente3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnBuscarCliente3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarCliente3ActionPerformed(evt);
            }
        });

        tblProductos.setBackground(new java.awt.Color(242, 242, 242));
        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Producto", "Codigo", "Precio Unitario", "Cantidad", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductos.setGridColor(new java.awt.Color(0, 0, 0));
        tblProductos.setSelectionBackground(new java.awt.Color(242, 242, 242));
        jScrollPane1.setViewportView(tblProductos);

        javax.swing.GroupLayout pnlCentralLayout = new javax.swing.GroupLayout(pnlCentral);
        pnlCentral.setLayout(pnlCentralLayout);
        pnlCentralLayout.setHorizontalGroup(
            pnlCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBuscarCliente3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        pnlCentralLayout.setVerticalGroup(
            pnlCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCentralLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(btnBuscarCliente3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCentralLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = -124;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 17, 0, 35);
        getContentPane().add(pnlCentral, gridBagConstraints);

        pnlSuperior2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255), 2), "Datos del Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(51, 153, 255))); // NOI18N

        lblCedula1.setText("Número de Cédula:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Información del Cliente:");

        lblNombre1.setText("Nombre:");

        lblCedulaCliente1.setText("Cedula:");

        lblCorreo1.setText("Correo:");

        btnBuscarCliente.setBackground(new java.awt.Color(51, 153, 255));
        btnBuscarCliente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBuscarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscarCliente.setText("Buscar Cliente");
        btnBuscarCliente.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSuperior2Layout = new javax.swing.GroupLayout(pnlSuperior2);
        pnlSuperior2.setLayout(pnlSuperior2Layout);
        pnlSuperior2Layout.setHorizontalGroup(
            pnlSuperior2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSuperior2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSuperior2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCedula1)
                    .addComponent(jLabel2)
                    .addComponent(lblNombre1)
                    .addComponent(lblCedulaCliente1)
                    .addComponent(lblCorreo1))
                .addContainerGap(140, Short.MAX_VALUE))
            .addGroup(pnlSuperior2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSuperior2Layout.createSequentialGroup()
                    .addContainerGap(158, Short.MAX_VALUE)
                    .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(30, 30, 30)))
        );
        pnlSuperior2Layout.setVerticalGroup(
            pnlSuperior2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSuperior2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCedula1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNombre1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCedulaCliente1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCorreo1)
                .addContainerGap(19, Short.MAX_VALUE))
            .addGroup(pnlSuperior2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlSuperior2Layout.createSequentialGroup()
                    .addGap(38, 38, 38)
                    .addComponent(btnBuscarCliente)
                    .addContainerGap(115, Short.MAX_VALUE)))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.ipady = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 26, 0, 0);
        getContentPane().add(pnlSuperior2, gridBagConstraints);

        btnGenerarFactura.setBackground(new java.awt.Color(255, 0, 51));
        btnGenerarFactura.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnGenerarFactura.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerarFactura.setText("Generar Factura");
        btnGenerarFactura.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnGenerarFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarFacturaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 37;
        gridBagConstraints.ipady = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(48, 12, 0, 0);
        getContentPane().add(btnGenerarFactura, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductoActionPerformed
         BuscarProducto();
    }//GEN-LAST:event_btnBuscarProductoActionPerformed

    private void btnGenerarFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarFacturaActionPerformed
        RegistrarFactura();
    }//GEN-LAST:event_btnGenerarFacturaActionPerformed

    private void btnBuscarCliente2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarCliente2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscarCliente2ActionPerformed

    private void btnAgregarFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarFacturaActionPerformed
        AgregarProducto();
    }//GEN-LAST:event_btnAgregarFacturaActionPerformed

    private void btnBuscarCliente3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarCliente3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscarCliente3ActionPerformed

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        BuscarPersona();
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

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
            java.util.logging.Logger.getLogger(SistemaFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SistemaFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SistemaFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SistemaFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SistemaFacturas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarFactura;
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnBuscarCliente2;
    private javax.swing.JButton btnBuscarCliente3;
    private javax.swing.JButton btnBuscarProducto;
    private javax.swing.JButton btnGenerarFactura;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblCedula1;
    private javax.swing.JLabel lblCedulaCliente1;
    private javax.swing.JLabel lblCodigoproducto;
    private javax.swing.JLabel lblCorreo1;
    private javax.swing.JLabel lblIVA;
    private javax.swing.JLabel lblNombre1;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblProducto;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlCentral;
    private javax.swing.JPanel pnlSuperior;
    private javax.swing.JPanel pnlSuperior1;
    private javax.swing.JPanel pnlSuperior2;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCedula1;
    private javax.swing.JTextField txtCodigoProducto;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
