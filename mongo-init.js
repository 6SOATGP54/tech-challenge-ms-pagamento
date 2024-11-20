// Criação do usuário root no banco admin
db = db.getSiblingDB("admin");
db.createUser({
    user: "admin",
    pwd: "admin2024",  // Senha do administrador
    roles: [{ role: "root", db: "admin" }]
});

// Criação do banco ms-pagamento e do usuário fiap
db = db.getSiblingDB("ms-pagamento");
db.createUser({
    user: "fiap",
    pwd: "tech2024",  // Senha do usuário fiap
    roles: [{ role: "readWrite", db: "ms-pagamento" }]  // Permissão de leitura e escrita no banco ms-pagamento
});