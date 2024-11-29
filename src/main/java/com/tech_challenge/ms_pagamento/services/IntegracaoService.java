package com.tech_challenge.ms_pagamento.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tech_challenge.ms_pagamento.dtos.models.CredencialModelDTO;
import com.tech_challenge.ms_pagamento.enums.EndpointsIntegracaoEnum;
import com.tech_challenge.ms_pagamento.util.Assembler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.tech_challenge.ms_pagamento.document.*;
import com.tech_challenge.ms_pagamento.dtos.*;
import com.tech_challenge.ms_pagamento.repository.*;
import com.tech_challenge.ms_pagamento.document.sustentacao.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.tech_challenge.ms_pagamento.util.Utils.prePersistPreUpdate;

@Service
public class IntegracaoService {

    public static final String PATH_WEBHOOK = "/api/integracoes/mercadoPago/pagamentoRecebido";

    @Autowired
    CredenciaisIntegracaoRepository credenciaisIntegracaoRepository;

    @Autowired
    LojaMercadoLivreRepository lojaMercadoLivreRepository;

    @Autowired
    CaixaMercadoPagoRepository caixaMercadoPagoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public Boolean cadastroCredenciais(CredencialModelDTO credenciaisAcesso) {

        CredenciaisAcesso entity = Assembler.toEntity(credenciaisAcesso, CredenciaisAcesso.class);

        entity.setTipoIntegracao(CredenciaisAcesso.ServicosIntegracao.MERCADO_PAGO);

        prePersistPreUpdate(entity);
        CredenciaisAcesso save = credenciaisIntegracaoRepository.save(entity);

        return save.getId() != null;
    }


    public EscopoLojaMercadoPago cadastroLojaMercadoLivre(EscopoLojaMercadoPago escopoLojaMercadoPago) {

        EscopoLojaMercadoPagoDTO escopoLojaMercadoPagoDTO = entityTODTO(escopoLojaMercadoPago);

        CredenciaisAcesso credenciaisAcesso = getCredenciaisAcesso(escopoLojaMercadoPago);

        return publicarLojaMercadoPago(escopoLojaMercadoPago,
                credenciaisAcesso,
                escopoLojaMercadoPagoDTO);

    }

    private EscopoLojaMercadoPago publicarLojaMercadoPago(EscopoLojaMercadoPago escopoLojaMercadoPago,
                                                          CredenciaisAcesso credenciaisAcesso,
                                                          EscopoLojaMercadoPagoDTO escopoLojaMercadoPagoDTO) {

        Map<Object, Object> parametros = Map.of("usuario_acesso", credenciaisAcesso.getUsuario());

        String urlCriarLoja = EndpointsIntegracaoEnum.CRIAR_LOJA.parametrosUrl(parametros);

        Object o =
                RequestServices.requestToMercadoPago(escopoLojaMercadoPagoDTO,
                        credenciaisAcesso,
                        urlCriarLoja,
                        HttpMethod.POST,
                        EndpointsIntegracaoEnum.CRIAR_LOJA);


        escopoLojaMercadoPago.setUserId(o instanceof Long ? String.valueOf(o) : null);

        Logger.getAnonymousLogger().info("Persistindo Loja no banco id retornado do agente externo" + escopoLojaMercadoPago.getUserId());

        if (escopoLojaMercadoPago.getUserId() != null) {
            return lojaMercadoLivreRepository.save(escopoLojaMercadoPago);
        }

        return new EscopoLojaMercadoPago();
    }

    private CredenciaisAcesso getCredenciaisAcesso(EscopoLojaMercadoPago escopoLojaMercadoPago) {
        return credenciaisIntegracaoRepository.findAll().get(0);
    }

    public EscopoLojaMercadoPagoDTO entityTODTO(EscopoLojaMercadoPago escopoLojaMercadoPago) {

        Map<Object, Object> businessHours = escopoLojaMercadoPago.getBusinessHours().stream()
                .collect(Collectors.toMap(DiaDaSemana::getDia,
                        diaDaSemana -> diaDaSemana.getIntervalos().stream()
                                .map(i -> new IntervalosDTO(i.getOpen(), i.getClose()))
                                .collect(Collectors.toList())));

        Map<Object, Object> location = getLocation(escopoLojaMercadoPago);


        return new EscopoLojaMercadoPagoDTO(
                escopoLojaMercadoPago.getName(),
                businessHours,
                location,
                escopoLojaMercadoPago.getExternalId());

    }

    private static Map<Object, Object> getLocation(EscopoLojaMercadoPago escopoLojaMercadoPago) {
        Location locationSalvo = escopoLojaMercadoPago.getLocation();
        Map<Object, Object> location = Map.of(
                "city_name", locationSalvo.getCityName(),
                "state_name", locationSalvo.getStateName(),
                "street_name", locationSalvo.getStreetName(),
                "street_number", locationSalvo.getStreetNumber(),
                "longitude", locationSalvo.getLongitude(),
                "latitude", locationSalvo.getLatitude(),
                "reference", locationSalvo.getReference()
        );
        return location;
    }

    public EscopoCaixaMercadoPago cadastrarCaixaLojaMercadoLivre(EscopoCaixaMercadoPago caixa) {

        EscopoLojaMercadoPago loja = lojaMercadoLivreRepository
                .findByUserId(caixa.getStore_id());

        caixa.setExternal_store_id(loja.getExternalId());
        caixa.setStore_id(loja.getUserId());

        CaixaDTO caixaLojaDTO = new CaixaDTO(caixa.getCategory(),
                caixa.getExternal_id(),
                caixa.getExternal_store_id(),
                caixa.getFixed_amount(),
                caixa.getName(),
                caixa.getStore_id());

        CredenciaisAcesso credenciaisAcesso = getCredenciaisAcesso(loja);

        String url = EndpointsIntegracaoEnum.CRIAR_CAIXA.getUrl();

        Object o =
                RequestServices.requestToMercadoPago(caixaLojaDTO,
                        credenciaisAcesso,
                        url,
                        HttpMethod.POST, EndpointsIntegracaoEnum.CRIAR_CAIXA);

        caixa.setIdAPI(o instanceof Long ? String.valueOf(o) : null);

        Logger.getAnonymousLogger().info("Persistindo CAIXA no banco id retornado do agente externo" + caixa.getIdAPI());

        if (caixa.getIdAPI() != null) {
            return caixaMercadoPagoRepository.save(caixa);
        }

        return new EscopoCaixaMercadoPago();
    }

    /**
     *
     * @param id
     * @param type
     *
     * Metodo para informado para o mercado pago, enviar o status do pagamento
     * estando efetuado, publica na fila de pagamento concluido ms-preparacao
     * vai ler e dar andamento no pedido
     */
    public void consultarPagamento(Object id, Object type) {

        if (type.equals("payment")) {

            Map<Object, Object> parametros = new HashMap<>();
            parametros.put("id", id);

            CredenciaisAcesso credenciaisAcesso =
                    credenciaisIntegracaoRepository.findAll().get(0);

            String url = EndpointsIntegracaoEnum.CONSULTAR_PAGAMENTO.parametrosUrl(parametros);

            Object o = RequestServices.requestToMercadoPago(null, credenciaisAcesso, url, HttpMethod.GET, EndpointsIntegracaoEnum.CONSULTAR_PAGAMENTO);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode;

            try {
                rootNode = objectMapper.readTree(o.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String status = rootNode.path("status").asText();
            String externalReference = rootNode.path("external_reference").asText();

            System.out.println(status);
            System.out.println(externalReference);

            Message message = new Message(("Pagamento efetuado " + externalReference).getBytes());
            rabbitTemplate.convertAndSend("pagamento.ex","", message);
        }
    }

    /**
     *
     * @param ordemVendaMercadoPagoDTO
     *
     * Escuta a fila pedido efetuado para gerar o QRCODE e publica na
     * fila par ao ms-pedido exibir para o usuário
     */
   // @RabbitListener(queues = "pedido.efetuado") //TODO: Fazer no ms de PEDIDO
    public void gerarQR(OrdemVendaMercadoPagoDTO ordemVendaMercadoPagoDTO) {
        CredenciaisAcesso credenciaisAcesso = credenciaisIntegracaoRepository.findAll().get(0);
        EscopoCaixaMercadoPago escopoCaixaMercadoPago = caixaMercadoPagoRepository
                .findByUsuario(credenciaisAcesso.getUsuario())
                .orElse(new EscopoCaixaMercadoPago());

        Map<Object, Object> parametros = new HashMap<>();

        parametros.put("user_id", credenciaisAcesso.getUsuario());
        parametros.put("external_pos_id", escopoCaixaMercadoPago.getExternal_id());

        String url = EndpointsIntegracaoEnum.GERARQRCODE.parametrosUrl(parametros);

        OrdemVendaMercadoPagoDTO emitirOrdemVenda = new OrdemVendaMercadoPagoDTO(ordemVendaMercadoPagoDTO.description(),
                ordemVendaMercadoPagoDTO.external_reference(),
                ordemVendaMercadoPagoDTO.items(),
                ordemVendaMercadoPagoDTO.title(),
                ordemVendaMercadoPagoDTO.total_amount(),
                credenciaisAcesso.getWebHook().concat(PATH_WEBHOOK));

        Object o = RequestServices.requestToMercadoPago(emitirOrdemVenda,
                credenciaisAcesso,
                url,
                HttpMethod.POST,
                EndpointsIntegracaoEnum.GERARQRCODE);

        String qrCode = (String) o;

        if(qrCode != null){
            rabbitTemplate.convertAndSend("pagamento.qrcode", qrCode);
        } else{
            throw new RuntimeException("Não foi possivel gerar QRCODE do pedido" + ordemVendaMercadoPagoDTO.external_reference());
        }
    }
}
