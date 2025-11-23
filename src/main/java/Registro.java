public class Registro {
    int id;
    String nome;
    String cpf;
    int prioridade;
    String data; // pode ser mantida como String para este exemplo

    public Registro(int id, String nome, String cpf, int prioridade, String data) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.prioridade = prioridade;
        this.data = data;
    }

    // comparação por prioridade (menor prioridade = vem primeiro)
    public static int compararPorPrioridade(Registro a, Registro b) {
        return Integer.compare(a.prioridade, b.prioridade);
    }
}
