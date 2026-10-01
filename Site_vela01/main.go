package main

import (
	"html/template"
	"log"
	"net/http"
)

type Vela struct {
	ID        string
	Nome      string
	Descricao string
	Detalhes  string
	Preco     string
	Imagem    string
}

func main() {
	fs := http.FileServer(http.Dir("static"))
	http.Handle("/static/", http.StripPrefix("/static/", fs))

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		velas := []Vela{
			{
				ID:        "1",
				Nome:      "Lavanda",
				Descricao: "Delicada e reconfortante, a vela Lavanda foi criada para trazer calma e equilíbrio ao seu dia. Seu aroma floral suave envolve o ambiente com uma sensação de tranquilidade, transformando qualquer momento mais leve, acolhedor e relaxante.",
				Detalhes:  "Desenvolvida artesanalmente com blend exclusivo de cera 100% vegetal (coco, arroz e palma) e pavio de madeira nobre que reproduz o crepitar suave de uma lareira.\n\nNotas Olfativas:\n• Saída: Lavanda francesa fresca, Bergamota e Eucalipto.\n• Corpo: Flores de Lavanda pura e Gerânio.\n• Fundo: Musk branco aveludado e Cedro.",
				Preco:     "R$ 65,00",
				Imagem:    "/static/images/lavanda.jpg",
			},
			{
				ID:        "2",
				Nome:      "Limão Siciliano",
				Descricao: "Energizante e vibrante, notas cítricas que renovam as energias do ambiente, proporcionando uma atmosfera limpa, alegre e revigorante.",
				Detalhes:  "Formulada para purificar e trazer frescura dinâmica aos espaços de convívio diário. Produzida com óleos essenciais cítricos de alta pureza.\n\nNotas Olfativas:\n• Saída: Limão Siciliano intenso e Capim-Limão.\n• Corpo: Flores Brancas delicadas e Verbena.\n• Fundo: Vetiver sofisticado e Âmbar leve.",
				Preco:     "R$ 65,00",
				Imagem:    "/static/images/limao.jpg",
			},
			{
				ID:        "3",
				Nome:      "Morango & Champanhe",
				Descricao: "Um toque adocicado e envolvente que desperta memórias afetivas e traz um aroma acolhedor, romântico e marcante para o espaço.",
				Detalhes:  "Uma fragrância de alta perfumaria que equilibra perfeitamente a doçura frutada com uma elegância reconfortante.\n\nNotas Olfativas:\n• Saída: Morango Silvestre colhido à mão e Framboesa.\n• Corpo: Folhas de Morango e Chantilly artesanal.\n• Fundo: Baunilha Gourmet de Madagascar e Caramelo.",
				Preco:     "R$ 65,00",
				Imagem:    "/static/images/morango.jpg",
			},
			{
				ID:        "4",
				Nome:      "Black Vanilla",
				Descricao: "A suavidade aconchegante da baunilha pura combinada com notas amadeiradas, criando um clima intimista, sofisticado e extremamente acolhedor.",
				Detalhes:  "O ápice do conforto olfativo. Ideal para noites frias ou momentos de autocuidado e leitura profunda.\n\nNotas Olfativas:\n• Saída: Baunilha Bourbon verdadeira e Fava Tonka.\n• Corpo: Orquídea Vanilla rara e Leite Quente.\n• Fundo: Sândalo indiano e Âmbar dourado.",
				Preco:     "R$ 65,00",
				Imagem:    "/static/images/vanilla.jpg",
			},
		}

		tmpl, err := template.ParseFiles("templates/index.html")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		tmpl.Execute(w, velas)
	})

	log.Println("Servidor a iniciar em http://localhost:8080 ...")
	err := http.ListenAndServe(":8080", nil)
	if err != nil {
		log.Fatal(err)
	}
}
