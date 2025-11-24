package com.pixelium.levelup.config;

import com.pixelium.levelup.model.Noticias;
import com.pixelium.levelup.repository.NoticiasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private NoticiasRepository noticiasRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificamos si la tabla está vacía para no duplicar datos cada vez que inicias
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
}