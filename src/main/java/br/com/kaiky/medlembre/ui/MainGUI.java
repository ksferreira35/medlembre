package br.com.kaiky.medlembre.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import br.com.kaiky.medlembre.model.Medicamento;
import br.com.kaiky.medlembre.service.FeriadoService;
import br.com.kaiky.medlembre.service.MedicamentoService;

public class MainGUI extends JFrame {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color COR_FUNDO        = new Color(243, 244, 246);
    private static final Color COR_PRIMARIA     = new Color(37,  99, 235);
    private static final Color COR_PRIMARIA_BG  = new Color(219, 234, 254);
    private static final Color COR_SUCESSO      = new Color(34,  197,  94);
    private static final Color COR_SUCESSO_BG   = new Color(220, 252, 231);
    private static final Color COR_PERIGO       = new Color(239,  68,  68);
    private static final Color COR_AVISO        = new Color(245, 158,  11);
    private static final Color COR_PENDENTE_FG  = new Color(194,  65,  12);
    private static final Color COR_PENDENTE_BG  = new Color(255, 237, 213);
    private static final Color COR_TEXTO        = new Color( 17,  24,  39);
    private static final Color COR_TEXTO_MUTED  = new Color(107, 114, 128);
    private static final Color COR_SUPERFICIE   = Color.WHITE;
    private static final Color COR_BORDA        = new Color(229, 231, 235);
    private static final Color COR_HEADER_BG    = new Color(249, 250, 251);

    // ── Serviços ──────────────────────────────────────────────────────────────
    private final MedicamentoService medicamentoService;
    private final FeriadoService feriadoService;

    // ── Componentes ───────────────────────────────────────────────────────────
    private MedicamentosTableModel tableModel;
    private JTable tabela;
    private JLabel lblStatus;
    private JLabel lblPendentes;
    private JLabel lblConcluidos;
    private final JLabel lblPagina    = new JLabel();
    private final JLabel versao       = new JLabel("Sistema: v1.2.0-STABLE");
    private JPanel painelFeriado;
    private JLabel lblFeriado;
    private JLabel lblSubtitulo;

    // Campos do formulário
    private JTextField txtNome;
    private JTextField txtDose;
    private JTextField txtHorario;

    public MainGUI() {
        this.medicamentoService = new MedicamentoService();
        this.feriadoService     = new FeriadoService();
        configurarJanela();
        construirUI();
        carregarDados();
        verificarFeriadoAsync();
    }

    // ── Configuração da Janela ────────────────────────────────────────────────

    private void configurarJanela() {
        setTitle("MedLembre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setPreferredSize(new Dimension(1050, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);
        setJMenuBar(criarMenuBar());
    }

    // ── Menu Bar ──────────────────────────────────────────────────────────────

    private JMenuBar criarMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(COR_SUPERFICIE);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA));

        JMenu mArquivo = menuItem("Arquivo");
        mArquivo.add(new JMenuItem("Novo Medicamento"));
        mArquivo.addSeparator();
        JMenuItem mSair = new JMenuItem("Sair");
        mSair.addActionListener(e -> System.exit(0));
        mArquivo.add(mSair);

        JMenuItem mFeriados = new JMenuItem("📅  Consultar Feriados");
        mFeriados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mFeriados.addActionListener(e -> exibirFeriados());

        bar.add(mArquivo);
        bar.add(mFeriados);
        bar.add(Box.createHorizontalGlue());
        return bar;
    }

    private JMenu menuItem(String label) {
        JMenu m = new JMenu(label);
        m.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        m.setForeground(COR_TEXTO);
        return m;
    }

    // ── Estrutura Principal ───────────────────────────────────────────────────

    private void construirUI() {
        setLayout(new BorderLayout(0, 0));
        add(criarBannerFeriado(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarRodape(),        BorderLayout.SOUTH);
        pack();
    }

    private JPanel criarBannerFeriado() {
        painelFeriado = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        painelFeriado.setBackground(new Color(254, 243, 199));
        painelFeriado.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(251, 191, 36)));
        painelFeriado.setVisible(false);

        lblFeriado = new JLabel();
        lblFeriado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFeriado.setForeground(new Color(120, 53, 15));
        painelFeriado.add(new JLabel("⚠️"));
        painelFeriado.add(lblFeriado);
        return painelFeriado;
    }

    private JSplitPane criarPainelCentral() {
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            criarSidebar(),
            criarAreaDireita()
        );
        split.setDividerLocation(310);
        split.setDividerSize(1);
        split.setBorder(null);
        split.setBackground(COR_BORDA);
        return split;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(COR_SUPERFICIE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COR_BORDA));
        sidebar.setPreferredSize(new Dimension(310, 0));

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(COR_SUPERFICIE);
        conteudo.setBorder(new EmptyBorder(24, 20, 20, 20));

        conteudo.add(criarSecaoCadastro());
        conteudo.add(Box.createRigidArea(new Dimension(0, 24)));
        conteudo.add(criarDivisor());
        conteudo.add(Box.createRigidArea(new Dimension(0, 16)));
        conteudo.add(criarSecaoGerenciamento());
        conteudo.add(Box.createVerticalGlue());

        sidebar.add(conteudo, BorderLayout.CENTER);
        sidebar.add(criarRodapeSidebar(), BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel criarSecaoCadastro() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("＋  Cadastrar Medicamento");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COR_TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createRigidArea(new Dimension(0, 16)));

        painel.add(rotuloForm("Nome do Medicamento"));
        painel.add(Box.createRigidArea(new Dimension(0, 4)));
        txtNome = criarCampoForm("Ex: Paracetamol");
        painel.add(txtNome);
        painel.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaDoseHorario = new JPanel(new GridLayout(1, 2, 10, 0));
        linhaDoseHorario.setOpaque(false);
        linhaDoseHorario.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaDoseHorario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JPanel grupoDose = new JPanel();
        grupoDose.setLayout(new BoxLayout(grupoDose, BoxLayout.Y_AXIS));
        grupoDose.setOpaque(false);
        grupoDose.add(rotuloForm("Dosagem"));
        grupoDose.add(Box.createRigidArea(new Dimension(0, 4)));
        txtDose = criarCampoForm("750mg");
        grupoDose.add(txtDose);

        JPanel grupoHorario = new JPanel();
        grupoHorario.setLayout(new BoxLayout(grupoHorario, BoxLayout.Y_AXIS));
        grupoHorario.setOpaque(false);
        grupoHorario.add(rotuloForm("Horário"));
        grupoHorario.add(Box.createRigidArea(new Dimension(0, 4)));
        txtHorario = criarCampoForm("--:--");
        txtHorario.setToolTipText("Formato HH:mm  ex: 08:00");
        grupoHorario.add(txtHorario);

        linhaDoseHorario.add(grupoDose);
        linhaDoseHorario.add(grupoHorario);
        painel.add(linhaDoseHorario);
        painel.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton btnSalvar = criarBotaoPrimario("💾  Salvar Registro");
        btnSalvar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSalvar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnSalvar.addActionListener(e -> cadastrarMedicamento());

        ActionListener enterCadastra = e -> cadastrarMedicamento();
        txtNome.addActionListener(enterCadastra);
        txtDose.addActionListener(enterCadastra);
        txtHorario.addActionListener(enterCadastra);

        painel.add(btnSalvar);
        return painel;
    }

    private JPanel criarSecaoGerenciamento() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("GERENCIAMENTO EM MASSA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titulo.setForeground(COR_TEXTO_MUTED);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnTomado = criarBotaoOutline("✔  Marcar como Tomado", COR_TEXTO);
        btnTomado.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTomado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnTomado.addActionListener(e -> marcarSelecionadoComoTomado());
        painel.add(btnTomado);
        painel.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton btnRemover = criarBotaoOutline("🗑  Remover Selecionados", COR_PERIGO);
        btnRemover.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRemover.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnRemover.addActionListener(e -> removerSelecionado());
        painel.add(btnRemover);
        return painel;
    }

    private JPanel criarDivisor() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        JSeparator sep = new JSeparator();
        sep.setForeground(COR_BORDA);
        p.add(sep);
        return p;
    }

    private JPanel criarRodapeSidebar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COR_HEADER_BG);
        p.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, COR_BORDA),
            new EmptyBorder(10, 16, 10, 16)
        ));

        lblStatus = new JLabel("Pronto.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(COR_TEXTO_MUTED);

        JLabel sincronizado = new JLabel("⊙  Sincronizado");
        sincronizado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sincronizado.setForeground(COR_SUCESSO);

        p.add(sincronizado, BorderLayout.WEST);
        p.add(lblStatus,    BorderLayout.EAST);
        return p;
    }

    // ── Área Direita ──────────────────────────────────────────────────────────

    private JPanel criarAreaDireita() {
        JPanel area = new JPanel(new BorderLayout(0, 0));
        area.setBackground(COR_FUNDO);
        area.add(criarCabecalhoDireito(), BorderLayout.NORTH);
        area.add(criarPainelTabela(),     BorderLayout.CENTER);
        return area;
    }

    private JPanel criarCabecalhoDireito() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_SUPERFICIE);
        painel.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA),
            new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel esquerda = new JPanel();
        esquerda.setLayout(new BoxLayout(esquerda, BoxLayout.Y_AXIS));
        esquerda.setOpaque(false);

        JLabel titulo = new JLabel("Meus Medicamentos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(COR_TEXTO);

        lblSubtitulo = new JLabel("Carregando...");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(COR_TEXTO_MUTED);

        esquerda.add(titulo);
        esquerda.add(Box.createRigidArea(new Dimension(0, 2)));
        esquerda.add(lblSubtitulo);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setOpaque(false);

        JTextField txtBusca = new JTextField(16);
        txtBusca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusca.setBorder(new CompoundBorder(
            new LineBorder(COR_BORDA, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        txtBusca.putClientProperty("JTextField.placeholderText", "Pesquisar...");

        JButton btnRefresh = new JButton("↻");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnRefresh.setForeground(COR_TEXTO_MUTED);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> carregarDados());

        direita.add(txtBusca);
        direita.add(btnRefresh);

        painel.add(esquerda, BorderLayout.WEST);
        painel.add(direita,  BorderLayout.EAST);
        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COR_FUNDO);
        wrapper.setBorder(new EmptyBorder(16, 16, 0, 16));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COR_SUPERFICIE);
        card.setBorder(new LineBorder(COR_BORDA, 1, true));

        tableModel = new MedicamentosTableModel();
        tabela = new JTable(tableModel);
        tabela.setRowHeight(52);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setSelectionBackground(COR_PRIMARIA_BG);
        tabela.setSelectionForeground(COR_TEXTO);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setGridColor(COR_BORDA);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setFillsViewportHeight(true);
        tabela.setBackground(COR_SUPERFICIE);

        JTableHeader header = tabela.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(COR_HEADER_BG);
        header.setForeground(COR_TEXTO_MUTED);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA));
        header.setReorderingAllowed(false);

        TableColumnModel cm = tabela.getColumnModel();
        cm.getColumn(0).setPreferredWidth(36);  cm.getColumn(0).setMaxWidth(44);
        cm.getColumn(1).setPreferredWidth(220);
        cm.getColumn(2).setPreferredWidth(100); cm.getColumn(2).setMaxWidth(120);
        cm.getColumn(3).setPreferredWidth(100); cm.getColumn(3).setMaxWidth(120);
        cm.getColumn(4).setPreferredWidth(130); cm.getColumn(4).setMaxWidth(150);

        tabela.setDefaultRenderer(Object.class, new MedicamentoRenderer());

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COR_SUPERFICIE);

        JLabel lblFim = new JLabel("Fim da lista de registros", SwingConstants.CENTER);
        lblFim.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblFim.setForeground(COR_BORDA);
        lblFim.setBorder(new EmptyBorder(10, 0, 10, 0));

        card.add(scroll, BorderLayout.CENTER);
        card.add(lblFim, BorderLayout.SOUTH);
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Rodapé ────────────────────────────────────────────────────────────────

    private JPanel criarRodape() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_SUPERFICIE);
        painel.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, COR_BORDA),
            new EmptyBorder(10, 16, 10, 16)
        ));

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        esquerda.setOpaque(false);
        lblPendentes  = criarBadgeLabel("● Pendentes: 0", COR_PERIGO);
        lblConcluidos = criarBadgeLabel("● Concluídos: 0", COR_SUCESSO);
        esquerda.add(lblPendentes);
        esquerda.add(lblConcluidos);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setOpaque(false);

        lblPagina.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPagina.setForeground(COR_TEXTO_MUTED);

        versao.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versao.setForeground(COR_TEXTO_MUTED);

        JButton btnAnterior = criarBotaoPaginacao("‹");
        JButton btnProximo  = criarBotaoPaginacao("›");

        direita.add(versao);
        direita.add(new JSeparator(JSeparator.VERTICAL));
        direita.add(lblPagina);
        direita.add(btnAnterior);
        direita.add(btnProximo);

        painel.add(esquerda, BorderLayout.WEST);
        painel.add(direita,  BorderLayout.EAST);
        return painel;
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    private JLabel rotuloForm(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(COR_TEXTO_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField criarCampoForm(String placeholder) {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBorder(new CompoundBorder(
            new LineBorder(COR_BORDA, 1, true),
            new EmptyBorder(7, 10, 7, 10)
        ));
        campo.setPreferredSize(new Dimension(0, 36));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setForeground(COR_TEXTO_MUTED);
        campo.setText(placeholder);
        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(COR_TEXTO);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (campo.getText().isBlank()) {
                    campo.setText(placeholder);
                    campo.setForeground(COR_TEXTO_MUTED);
                }
            }
        });
        return campo;
    }

    private String valorCampo(JTextField campo, String placeholder) {
        String v = campo.getText().trim();
        return v.equals(placeholder) ? "" : v;
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed()  ? COR_PRIMARIA.darker() :
                          getModel().isRollover() ? new Color(29, 78, 216) : COR_PRIMARIA;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 38));
        return btn;
    }

    private JButton criarBotaoOutline(String texto, Color corTexto) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(corTexto.getRed(), corTexto.getGreen(), corTexto.getBlue(), 15));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(COR_BORDA);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(corTexto);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 36));
        return btn;
    }

    private JButton criarBotaoPaginacao(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(COR_TEXTO_MUTED);
        btn.setPreferredSize(new Dimension(28, 28));
        btn.setBorder(new LineBorder(COR_BORDA, 1, true));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel criarBadgeLabel(String texto, Color cor) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(cor);
        return lbl;
    }

    // ── Lógica de Negócio ─────────────────────────────────────────────────────

    private void carregarDados() {
        List<Medicamento> lista = medicamentoService.listar();
        tableModel.setMedicamentos(lista);

        long pendentes  = lista.stream().filter(m -> !m.isTomadoHoje()).count();
        long concluidos = lista.stream().filter(Medicamento::isTomadoHoje).count();

        lblPendentes.setText("● Pendentes: " + pendentes);
        lblConcluidos.setText("● Concluídos: " + concluidos);
        lblPagina.setText("Página 1 de 1");

        lblSubtitulo.setText(
    pendentes == 0
        ? "Nenhum medicamento pendente para hoje."
        : "Você possui "
            + pendentes
            + " medicamento"
            + (pendentes == 1 ? "" : "s")
            + " agendado"
            + (pendentes == 1 ? "" : "s")
            + " para hoje."
  );
    }

    private void cadastrarMedicamento() {
        String nome    = valorCampo(txtNome,    "Ex: Paracetamol");
        String dose    = valorCampo(txtDose,    "750mg");
        String horario = valorCampo(txtHorario, "--:--");

        try {
            medicamentoService.cadastrar(nome, dose, horario);
            txtNome.setText("Ex: Paracetamol"); txtNome.setForeground(COR_TEXTO_MUTED);
            txtDose.setText("750mg");           txtDose.setForeground(COR_TEXTO_MUTED);
            txtHorario.setText("--:--");        txtHorario.setForeground(COR_TEXTO_MUTED);
            txtNome.requestFocus();
            carregarDados();
            exibirSucesso("\"" + nome + "\" cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            exibirErro(e.getMessage());
        } catch (IllegalStateException e) {
            exibirErro("Erro de persistência: " + e.getMessage());
        }
    }

    private void marcarSelecionadoComoTomado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { exibirAviso("Selecione um medicamento na lista."); return; }

        int id = tableModel.getIdNaLinha(linha);
        Medicamento med = medicamentoService.listar().stream()
            .filter(m -> m.getId() == id).findFirst().orElse(null);

        if (med != null && med.isTomadoHoje()) {
            exibirAviso("\"" + med.getNome() + "\" já está marcado como tomado.");
            return;
        }

        if (medicamentoService.marcarComoTomado(String.valueOf(id))) {
            carregarDados();
            exibirSucesso("Marcado como tomado!");
        } else {
            exibirErro("Medicamento não encontrado.");
        }
    }

    private void removerSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { exibirAviso("Selecione um medicamento na lista."); return; }

        int id      = tableModel.getIdNaLinha(linha);
        String nome = tableModel.getNomeNaLinha(linha);

        int r = JOptionPane.showConfirmDialog(this,
            "Remover \"" + nome + "\"?\nEsta ação não pode ser desfeita.",
            "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (r != JOptionPane.YES_OPTION) { exibirStatus("Remoção cancelada.", COR_TEXTO_MUTED); return; }

        if (medicamentoService.removerPorId(id)) {
            carregarDados();
            exibirSucesso("\"" + nome + "\" removido.");
        } else {
            exibirErro("Medicamento não encontrado.");
        }
    }

    private void exibirFeriados() {
        String ano = String.valueOf(LocalDate.now().getYear());
        JDialog dialog = new JDialog(this, "Feriados Nacionais " + ano, true);
        dialog.setSize(420, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(COR_FUNDO);

        JLabel titulo = new JLabel("Feriados Nacionais " + ano, SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(COR_TEXTO);
        titulo.setBorder(new EmptyBorder(20, 16, 12, 16));

        JTextArea area = new JTextArea("Buscando feriados...");
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBackground(COR_SUPERFICIE);
        area.setForeground(COR_TEXTO);
        area.setBorder(new EmptyBorder(8, 16, 8, 16));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA));

        JButton btnFechar = criarBotaoPrimario("Fechar");
        btnFechar.setPreferredSize(new Dimension(120, 36));
        btnFechar.addActionListener(e -> dialog.dispose());

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBackground(COR_FUNDO);
        rodape.setBorder(new EmptyBorder(12, 16, 16, 16));
        rodape.add(btnFechar);

        dialog.add(titulo, BorderLayout.NORTH);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.add(rodape, BorderLayout.SOUTH);

        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override protected List<String> doInBackground() throws Exception {
                return feriadoService.listarFeriadosDoAno(ano);
            }
            @Override protected void done() {
                try {
                    List<String> feriados = get();
                    area.setText(feriados.isEmpty()
                        ? "Não foi possível obter os feriados.\nVerifique sua conexão."
                        : String.join("\n", feriados));
                    area.setCaretPosition(0);
                } catch (InterruptedException | ExecutionException e) {
                    area.setText("Erro ao buscar feriados:\n" + e.getCause().getMessage());
                }
            }
        };
        worker.execute();
        dialog.setVisible(true);
    }

    private void verificarFeriadoAsync() {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override protected String doInBackground() { return feriadoService.verificarHoje(); }
            @Override protected void done() {
                try {
                    String f = get();
                    if (f != null) {
                        lblFeriado.setText(
                              "Hoje é feriado: "
                                  + f
                                  + " — Verifique se seus medicamentos estão em estoque!"
                          );
                        painelFeriado.setVisible(true);
                        revalidate();
                    }
                } catch (InterruptedException | ExecutionException ignored) { }
            }
        };
        worker.execute();
    }

    // ── Feedback ──────────────────────────────────────────────────────────────

        private void exibirSucesso(String msg) {
        exibirStatus("✔  " + msg, COR_SUCESSO);
    }

    private void exibirErro(String msg) {
        exibirStatus("✕  " + msg, COR_PERIGO);
        Toolkit.getDefaultToolkit().beep();
    }

    private void exibirAviso(String msg) {
        exibirStatus("⚠  " + msg, COR_AVISO);
    }

    private void exibirStatus(String msg, Color cor) {
        lblStatus.setText(msg);
        lblStatus.setForeground(cor);
        Timer t = new Timer(4000, e -> {
            lblStatus.setText("Pronto.");
            lblStatus.setForeground(COR_TEXTO_MUTED);
        });
        t.setRepeats(false);
        t.start();
    }

    // ── TableModel ────────────────────────────────────────────────────────────

    private static class MedicamentosTableModel extends AbstractTableModel {
        private static final String[] COLUNAS = {"", "MEDICAMENTO", "DOSAGEM", "HORÁRIO", "STATUS"};
        private List<Medicamento> dados = List.of();

        void setMedicamentos(List<Medicamento> lista) { this.dados = lista; fireTableDataChanged(); }
        int    getIdNaLinha(int r)   { return dados.get(r).getId(); }
        String getNomeNaLinha(int r) { return dados.get(r).getNome(); }

        @Override public int    getRowCount()          { return dados.size(); }
        @Override public int    getColumnCount()       { return COLUNAS.length; }
        @Override public String getColumnName(int col) { return COLUNAS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Medicamento m = dados.get(row);
            return switch (col) {
                case 0 -> "";
                case 1 -> m;
                case 2 -> m.getDose();
                case 3 -> m.getHorario();
                case 4 -> m.isTomadoHoje() ? "TOMADO" : "PENDENTE";
                default -> "";
            };
        }
    }

    // ── Cell Renderer ─────────────────────────────────────────────────────────

    private class MedicamentoRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            boolean tomado = "TOMADO".equals(table.getValueAt(row, 4));

            if (column == 1 && value instanceof Medicamento m) {
                JPanel cell = new JPanel();
                cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
                cell.setBorder(new EmptyBorder(0, 12, 0, 12));
                cell.setBackground(isSelected ? COR_PRIMARIA_BG : COR_SUPERFICIE);

                JLabel nome = new JLabel(m.getNome());
                nome.setFont(new Font("Segoe UI", Font.BOLD, 13));
                nome.setForeground(COR_TEXTO);

                JLabel sub = new JLabel("Uso Contínuo");
                sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                sub.setForeground(COR_TEXTO_MUTED);

                cell.add(Box.createVerticalGlue());
                cell.add(nome);
                cell.add(sub);
                cell.add(Box.createVerticalGlue());
                return cell;
            }

            if (column == 4) {
                JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
                badge.setBackground(isSelected ? COR_PRIMARIA_BG : COR_SUPERFICIE);

                JLabel badgeRounded = new JLabel(tomado ? "● TOMADO" : "● PENDENTE") {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                badgeRounded.setFont(new Font("Segoe UI", Font.BOLD, 11));
                badgeRounded.setOpaque(false);
                badgeRounded.setBorder(new EmptyBorder(4, 12, 4, 12));
                badgeRounded.setBackground(tomado ? COR_SUCESSO_BG : COR_PENDENTE_BG);
                badgeRounded.setForeground(tomado ? new Color(21, 128, 61) : COR_PENDENTE_FG);

                badge.add(badgeRounded);
                return badge;
            }

            if (column == 2) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
                p.setBackground(isSelected ? COR_PRIMARIA_BG : COR_SUPERFICIE);
                JLabel dose = new JLabel(String.valueOf(value)) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(229, 231, 235));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                dose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                dose.setForeground(COR_TEXTO);
                dose.setOpaque(false);
                dose.setBorder(new EmptyBorder(4, 10, 4, 10));
                p.add(dose);
                return p;
            }

            if (column == 3) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                p.setBackground(isSelected ? COR_PRIMARIA_BG : COR_SUPERFICIE);
                JLabel ico  = new JLabel("⏱");
                ico.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                ico.setForeground(COR_TEXTO_MUTED);
                JLabel hora = new JLabel(String.valueOf(value));
                hora.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                hora.setForeground(COR_TEXTO);
                p.add(ico);
                p.add(hora);
                return p;
            }

            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            comp.setBackground(isSelected ? COR_PRIMARIA_BG : COR_SUPERFICIE);
            comp.setForeground(COR_TEXTO_MUTED);
            ((JLabel) comp).setBorder(new EmptyBorder(0, 8, 0, 0));
            return comp;
        }
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException
                    | UnsupportedLookAndFeelException ignored) {
            }
            new MainGUI().setVisible(true);
        });
    }
}
