package com.Estoque.repositories;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Repository
public class LogsRepository {
    
	private final DatabaseReference databaseReference;
    
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uu - HH:mm");
	private DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss");
    @Autowired
	public LogsRepository(FirebaseDatabase firebaseDatabase) {
		this.databaseReference = firebaseDatabase.getReference("logs");
	}
    
    public void logUserAction(String message) throws Exception {
        LocalDateTime dateAndHour = java.time.LocalDateTime.now();
        Map<String, String> tokens = TokenAuthentication.loadToken();
        String idToken = tokens.get("authToken");
        String logKey = dateAndHour.format(dtf2);

        // Verifique se o token é válido
        if (idToken == null || idToken.isEmpty()) {
            throw new Exception("Token não encontrado ou inválido.");
        }

        String email = getLoggedInUserEmail(idToken);

        if (email == null) {
            throw new Exception("Usuário não encontrado com o token fornecido.");
        }
        
        
        Map<String, Object> logData = new HashMap<>();
        logData.put("email", email);
        logData.put("dataHora", dateAndHour.format(dtf));
        logData.put("mensagem", message);
        
        // Salvar log no Firebase
        databaseReference.child("datas").child(logKey).setValueAsync(logData);
    }
    
    public static String getLoggedInUserEmail(String idToken) throws Exception {
        // Verifica e deco difica o ID token
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        
        // Obtém o UID do usuário a partir do token
        String uid = decodedToken.getUid();
        
        // Usando o UID, podemos obter o email do usuário
        UserRecord user = FirebaseAuth.getInstance().getUser(uid);
        
        if (user != null) {
            return user.getEmail();  // Retorna o email do usuário
        } else {
            return null;  // Nenhum usuário encontrado com o UID
        }
    }
    
    public List<Map<String, Object>> getLogsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> logs = new ArrayList<>();
        
        try {
            DateTimeFormatter dtfMonth = DateTimeFormatter.ofPattern("MM");
            DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm:ss");

            // Convertendo as datas de início e fim para chaves
            String startYear = String.valueOf(startDate.getYear());
            String startMonth = startDate.format(dtfMonth);
            String startDay = String.valueOf(startDate.getDayOfMonth());

            String endYear = String.valueOf(endDate.getYear());
            String endMonth = endDate.format(dtfMonth);
            String endDay = String.valueOf(endDate.getDayOfMonth());

            // Log para verificar as datas
            System.out.println("Consultando logs de " + startYear + "/" + startMonth + "/" + startDay + " até " + endYear + "/" + endMonth + "/" + endDay);

            // Transformando LocalDate em LocalDateTime (início e fim do dia)
            LocalDateTime startDateTime = startDate.atStartOfDay();  // Começo do dia
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);  // Final do dia

            // Formatando as datas para o formato desejado de hora
            String startTime = startDateTime.format(dtfTime); // "00:00:00"
            String endTime = endDateTime.format(dtfTime);     // "23:59:59"

            // Realizando a consulta no banco de dados para cada dia no intervalo
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                String currentYear = String.valueOf(currentDate.getYear());
                String currentMonth = currentDate.format(dtfMonth);
                String currentDay = String.valueOf(currentDate.getDayOfMonth());

                // Consultando logs para o dia atual
                databaseReference.child("datas")
                        .child(currentYear)
                        .child(currentMonth)
                        .child(currentDay)
                        .orderByKey()
                        .startAt(startTime)  // Hora de início
                        .endAt(endTime)      // Hora de término
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Verificando se dados foram retornados
                                if (!dataSnapshot.exists()) {
                                    System.out.println("Nenhum dado encontrado para o dia " + currentYear + "/" + currentMonth + "/" + currentDay);
                                } else {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        logs.add((Map<String, Object>) snapshot.getValue());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.err.println("Erro ao recuperar logs: " + databaseError.getMessage());
                            }
                        });

                // Avança para o próximo dia
                currentDate = currentDate.plusDays(1);
            }

            // Espera a consulta terminar (simulação de espera - melhor usar uma abordagem assíncrona)
            Thread.sleep(2000); 
        } catch (Exception e) {
            e.printStackTrace();
        }

        return logs;
    }



    
    public byte[] generateLogsPDF(List<Map<String, Object>> logs, String filePath) {
        if (logs == null || logs.isEmpty()) {
            System.out.println("Nenhum log para gerar.");
            return null;  // Não faz nada se não houver logs.
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] pdfBytes = new byte[0]; // Inicializa o byte array para os bytes do PDF

        try {
            // Criar o documento PDF
            Document document = new Document();
            
            // Criar o escritor do PDF e direcionar a saída para o ByteArrayOutputStream
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            BaseFont baseFont = BaseFont.createFont("Times-Roman", BaseFont.WINANSI, BaseFont.EMBEDDED);
            Font fontTitle = new Font(baseFont, 30, Font.BOLD, BaseColor.BLACK);
            Paragraph title = new Paragraph("Logs de Ações dos Usuários", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(20f); 
            title.setSpacingAfter(40f);  

            document.add(title);
            document.setPageSize(PageSize.A4);

            // Criar uma tabela com 3 colunas (Email, Data/Hora, Mensagem)
            PdfPTable table = new PdfPTable(3);
            float[] columnWidths = { 2f, 1.3f, 2f }; // Proporção das colunas: 1 para Email, 1 para Data/Hora, 3 para Mensagem
            table.setWidths(columnWidths);
            table.addCell(createCell("Email", 16, Font.BOLD));
            table.addCell(createCell("Data/Hora", 16, Font.BOLD));
            table.addCell(createCell("Mensagem", 16, Font.BOLD));

            // Adicionar os dados dos logs à tabela
            for (Map<String, Object> log : logs) {
                String email = (log.get("email") != null) ? log.get("email").toString() : "Não informado";
                String dateAndHour = (log.get("dataHora") != null) ? log.get("dataHora").toString() : "Não informado";
                String message = (log.get("mensagem") != null) ? log.get("mensagem").toString() : "Não informada";

                table.addCell(createCell(email, 13, Font.NORMAL));
                table.addCell(createCell(dateAndHour, 13, Font.NORMAL));
                table.addCell(createCell(message, 13, Font.NORMAL));
            }

            // Adicionar a tabela ao documento
            document.add(table);

            // Fechar o documento e finalizar a escrita
            document.close();

            // Converte o conteúdo do ByteArrayOutputStream para um array de bytes
            pdfBytes = byteArrayOutputStream.toByteArray();

            // Verifica se o PDF foi gerado corretamente
            if (pdfBytes.length > 0) {

                // Salvar o PDF no caminho especificado
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    fos.write(pdfBytes); // Salvar os bytes do PDF no arquivo
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("PDF gerado com sucesso no caminho: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retorna os bytes do PDF gerado (caso precise usá-los em outro lugar)
        return pdfBytes;
    }

    private PdfPCell createCell(String text, int fontSize, int fontStyle) {
        try {
            BaseFont baseFont = BaseFont.createFont("Times-Roman", BaseFont.WINANSI, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, fontSize, fontStyle, BaseColor.BLACK);
            PdfPCell cell = new PdfPCell(new Phrase(text, font));
            cell.setPadding(5);
            return cell;
        } catch (Exception e) {
            e.printStackTrace();
            return new PdfPCell(new Phrase(text)); // Fallback
        }
    }
    
}
