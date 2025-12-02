package com.pixelium.levelup.config;

import com.pixelium.levelup.model.Noticias;
import com.pixelium.levelup.model.Producto;
import com.pixelium.levelup.model.Usuario; // Importar el modelo Usuario
import com.pixelium.levelup.repository.NoticiasRepository;
import com.pixelium.levelup.repository.ProductoRepository;
import com.pixelium.levelup.repository.UsuarioRepository; // Importar el repositorio de Usuario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar PasswordEncoder
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private NoticiasRepository noticiasRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // --- NUEVAS DEPENDENCIAS ---
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectar para cifrar contraseñas
    // ---------------------------

    @Override
    public void run(String... args) throws Exception {
        cargarUsuarios(); // Cargar primero los usuarios
        cargarNoticias();
        cargarProductos();
    }

    // --- NUEVO MÉTODO PARA CARGAR USUARIOS ---
    private void cargarUsuarios() {
        if (usuarioRepository.count() == 0) {
            System.out.println("--- Cargando Usuarios Iniciales ---");

            // 1. USUARIO ADMINISTRADOR
            Usuario admin = new Usuario();
            admin.setNombre("Administrador LevelUp");
            admin.setRut("11111111-1");
            admin.setTelefono("987654321");
            admin.setFechaNacimiento("1990-01-01");
            admin.setCorreo("admin@duoc.cl");
            // ESENCIAL: Cifrar la contraseña
            admin.setPassword(passwordEncoder.encode("12345"));
            admin.setComuna("Santiago");
            admin.setRole("ADMIN"); // Asignar rol de Administrador
            admin.setAvatarSrc("admin_avatar.png");

            // 2. USUARIO NORMAL
            Usuario user = new Usuario();
            user.setNombre("Usuario Normal");
            user.setRut("22222222-2");
            user.setTelefono("912345678");
            user.setFechaNacimiento("2000-05-15");
            user.setCorreo("user@duoc.cl");
            // ESENCIAL: Cifrar la contraseña
            user.setPassword(passwordEncoder.encode("12345"));
            user.setComuna("Providencia");
            user.setRole("USER"); // Asignar rol de Usuario normal
            user.setAvatarSrc("user_avatar.png");

            usuarioRepository.saveAll(Arrays.asList(admin, user));
            System.out.println("--- Usuarios 'admin@levelup.cl' (ADMIN) y 'user@levelup.cl' (USER) cargados exitosamente ---");
        }
    }
    // ------------------------------------------

    private void cargarNoticias() {
        if (noticiasRepository.count() == 0) {
            System.out.println("--- Carga de Datos Iniciales Comienza ---");

            // Noticia 1
            Noticias n1 = new Noticias();
            n1.setTitulo("Nuevo RPG sorprende con mecánicas innovadoras y comunidad activa");
            n1.setDetalle("El nuevo título \"Eternal Realms: Awakening\", lanzado por el estudio independiente BrightPixel, ha causado sensación en la comunidad gamer. Su propuesta combina un mundo abierto dinámico con un sistema de decisiones que realmente afectan la historia y la evolución de los personajes.\n" +
                    "\n" +
                    "Lo que más ha llamado la atención es su mecánica de eventos comunitarios, donde los jugadores de todo el mundo influyen en el curso del juego en tiempo real. Además, la personalización de personajes y la banda sonora orquestada han recibido elogios en foros y redes sociales.\n" +
                    "\n" +
                    "Según datos preliminares, el juego alcanzó 2 millones de descargas en su primera semana, convirtiéndose en uno de los lanzamientos indie más exitosos del año.");
            n1.setImagen("ER3.webp"); // <--- OJO AQUÍ: Debe coincidir con el archivo físico
            noticiasRepository.save(n1);

            // Noticia 2
            Noticias n2 = new Noticias();
            n2.setTitulo("Récord en campeonato mundial de eSports");
            n2.setDetalle("El pasado fin de semana, el World eSports Championship 2025 se convirtió en uno de los eventos más vistos en la historia del gaming competitivo. Transmitido desde Tokio, el torneo reunió a los mejores equipos de títulos como Valorant, League of Legends y Counter-Strike 2.\n" +
                    "\n" +
                    "La final de Valorant entre los equipos SkyBlaze y IronWolves alcanzó un pico de 8,5 millones de espectadores simultáneos, superando la marca histórica del año anterior.\n" +
                    "\n" +
                    "Los organizadores anunciaron además que, gracias a los nuevos acuerdos de patrocinio, el premio acumulado llegó a los 15 millones de dólares, consolidando el campeonato como uno de los más lucrativos del sector.\n" +
                    "\n" +
                    "Más allá de las cifras, lo que marcó el evento fue la enorme participación de la comunidad: foros, memes y transmisiones alternativas con streamers de renombre mantuvieron viva la conversación durante todo el fin de semana.");
            n2.setImagen("eSport.webp");
            noticiasRepository.save(n2);

            System.out.println("--- Noticias precargadas exitosamente ---");
        }
    }

    private void cargarProductos() {
        if (productoRepository.count() == 0) {
            System.out.println("--- Cargando Productos Iniciales ---");

            Producto p1 = new Producto();
            p1.setTitle("Catan");
            p1.setDescription("Un clásico juego de estrategia donde los jugadores compiten por colonizar y expandirse en la isla de Catan. Ideal para 3-4 jugadores.");
            p1.setCategory("Juegos de Mesa");
            p1.setPrice(29990);
            p1.setImageSrc("catan.jpg");
            p1.setImageSrc2("catan-detalle-1.png");
            p1.setImageSrc3("catan-detalle-2.png");
            p1.setImageSrc4("catan-detalle-3.jpg");

            Producto p2 = new Producto();
            p2.setTitle("Carcassonne");
            p2.setDescription("Un juego de colocación de fichas donde los jugadores construyen el paisaje alrededor de la fortaleza medieval de Carcassonne.");
            p2.setCategory("Juegos de Mesa");
            p2.setPrice(24990);
            p2.setImageSrc("carcasone.webp");
            p2.setImageSrc2("carcasone-detalle-1.png");
            p2.setImageSrc3("carcasone-detalle-2.png");
            p2.setImageSrc4("carcasone-detalle-3.png");

            Producto p3 = new Producto();
            p3.setTitle("Controlador Inalámbrico Xbox Series X");
            p3.setDescription("Ofrece una experiencia de juego cómoda con botones mapeables y una respuesta táctil mejorada. Compatible con consolas Xbox y PC.");
            p3.setCategory("Accesorios");
            p3.setPrice(59990);
            p3.setImageSrc("mando xbox.webp");
            p3.setImageSrc2("mando-xbox-detalle-1.png");
            p3.setImageSrc3("mando-xbox-detalle-2.webp");
            p3.setImageSrc4("mando-xbox-detalle-3.png");

            Producto p4 = new Producto();
            p4.setTitle("Auriculares Gamer HyperX Cloud II");
            p4.setDescription("Proporcionan un sonido envolvente de calidad con un micrófono desmontable y almohadillas de espuma viscoelástica.");
            p4.setCategory("Accesorios");
            p4.setPrice(79990);
            p4.setImageSrc("HYPERXX.png");
            p4.setImageSrc2("audifono-detalle-1.png");
            p4.setImageSrc3("audifono-detalle-2.png");
            p4.setImageSrc4("audifono-detalle-3.png");

            Producto p5 = new Producto();
            p5.setTitle("PlayStation 5");
            p5.setDescription("La consola de última generación de Sony, que ofrece gráficos impresionantes y tiempos de carga ultrarrápidos.");
            p5.setCategory("Consolas");
            p5.setPrice(549990);
            p5.setImageSrc("play_5.webp");
            p5.setImageSrc2("play-detalle-1.png");
            p5.setImageSrc3("play-detalle-2.png");
            p5.setImageSrc4("play-detalle-3.png");

            Producto p6 = new Producto();
            p6.setTitle("PC Gamer ASUS ROG Strix");
            p6.setDescription("Un potente equipo diseñado para los gamers más exigentes, equipado con los últimos componentes.");
            p6.setCategory("Computadores Gamers");
            p6.setPrice(1299990);
            p6.setImageSrc("PC_ASUS.webp");
            p6.setImageSrc2("pc-asus-detalle-1.png");
            p6.setImageSrc3("pc-asus-detalle-2.png");
            p6.setImageSrc4("pc-asus-detalle-3.png");

            Producto p7 = new Producto();
            p7.setTitle("Silla Gamer Secretlab Titan");
            p7.setDescription("Diseñada para el máximo confort, esta silla ofrece un soporte ergonómico y personalización ajustable.");
            p7.setCategory("Sillas Gamers");
            p7.setPrice(349990);
            p7.setImageSrc("silla gamer.webp");
            p7.setImageSrc2("silla_detalle_1.png");
            p7.setImageSrc3("silla_detalle_2.png");
            p7.setImageSrc4("silla_detalle_3.png");

            Producto p8 = new Producto();
            p8.setTitle("Mouse Gamer Logitech G502 HERO");
            p8.setDescription("Con sensor de alta precisión y botones personalizables, este mouse es ideal para gamers que buscan un control preciso.");
            p8.setCategory("Mouse");
            p8.setPrice(49990);
            p8.setImageSrc("mouse_gamer.webp");
            p8.setImageSrc2("mouse_detalle_1.png");
            p8.setImageSrc3("mouse_detalle_2.png");
            p8.setImageSrc4("mouse_detalle_3.png");

            Producto p9 = new Producto();
            p9.setTitle("Mousepad Razer Goliathus Extended Chroma");
            p9.setDescription("Ofrece un área de juego amplia con iluminación RGB personalizable, asegurando una superficie suave.");
            p9.setCategory("Mousepad");
            p9.setPrice(29990);
            p9.setImageSrc("mousepad.jpg");
            p9.setImageSrc2("mousepad_detalle_1.jpg");
            p9.setImageSrc3("mousepad_detalle_2.png");
            p9.setImageSrc4("mousepad_detalle_3.jpg");

            Producto p10 = new Producto();
            p10.setTitle("Polera Gamer Personalizada Level-Up");
            p10.setDescription("Una camiseta cómoda y estilizada, con la posibilidad de personalizarla con tu gamer tag.");
            p10.setCategory("Poleras Personalizadas");
            p10.setPrice(14990);
            p10.setImageSrc("Camiseta-level.jpg");
            p10.setImageSrc2("polera_gamer_detalle_1.png");
            p10.setImageSrc3("polera_gamer_detalle_2.png");
            p10.setImageSrc4("polera_gamer_detalle_3.png");

            productoRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10));

            System.out.println("--- Productos cargados exitosamente ---");
        }
    }
}