package com.example.pawpalclinic.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pawpalclinic.R;

import io.noties.markwon.Markwon;

public class apropos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apropos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView markdownTextView = findViewById(R.id.markdown_text);
        Markwon markwon = Markwon.create(this);

        String markdownContent = "# À propos de PawPal Clinic\n\n" +
                "Bienvenue dans **PawPal Clinic**, l'application mobile conçue pour simplifier la vie des propriétaires d'animaux et assurer les meilleurs soins pour leurs compagnons. Notre mission est de connecter les propriétaires d'animaux et les vétérinaires à travers une plateforme intuitive et moderne.\n\n" +
                "---\n\n" +
                "## **Qui sommes-nous ?**\n\n" +
                "Nous sommes une équipe passionnée de développeurs et d'amoureux des animaux, engagés à transformer la gestion des soins vétérinaires grâce à la technologie.\n\n" +
                "- [**KLALI Firas**](https://www.linkedin.com/in/klali-firas/)\n" +
                "- [**FATNASSI Roua**](https://www.linkedin.com/in/roua-fatnassi-73b495256/)\n" +
                "- [**ISSAOUI Med Amine**](https://www.linkedin.com/in/mohamed-amine-issaoui-a1b629256/)\n\n" +
                "Encadré par notre enseignante [**Mme Mariem Baccouche**](https://www.linkedin.com/in/mariem-baccouche-35791a241/) et notre superviseur [**Mr. Faouzi Maddouri**](https://www.linkedin.com/in/faouzi-maddouri-56598110/), qui nous guident dans ce projet avec expertise et bienveillance.\n\n" +
                "---\n\n" +
                "## **Notre Mission**\n\n" +
                "1. **Simplifier la gestion des soins des animaux** : Planifiez vos rendez-vous, suivez l'historique médical de vos animaux et recevez des rappels importants.\n" +
                "2. **Connecter les propriétaires et les vétérinaires** : Facilitez l'accès aux services vétérinaires et aux produits essentiels.\n" +
                "3. **Améliorer les soins animaliers** : Offrir une solution moderne pour suivre les traitements et les besoins nutritionnels de vos animaux.\n\n" +
                "---\n\n" +
                "## **Fonctionnalités principales**\n\n" +
                "- **Rendez-vous** : Planifiez et suivez les rendez-vous directement depuis l'application.\n" +
                "- **Profils d'animaux** : Accédez à toutes les informations importantes sur vos animaux (âge, race, historique médical).\n" +
                "- **Produits** : Commandez facilement de la nourriture et des accessoires pour vos compagnons.\n" +
                "- **Rappels** : Recevez des notifications pour les vaccinations et traitements nécessaires.\n\n" +
                "---\n\n" +
                "## **Contactez-nous**\n\n" +
                "Email : [**pawpalclinic@gmail.com**](mailto:pawpalclinic@gmail.com)\n" +
                "Téléphone : [**+216 96506517**](tel:+21696506517)\n\n" +
                "Merci d'utiliser **PawPal Clinic** pour prendre soin de vos animaux ! ❤️";

        markwon.setMarkdown(markdownTextView, markdownContent);
    }
}