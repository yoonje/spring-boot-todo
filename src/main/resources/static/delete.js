$('.delete-btn').on('click', showDeleteModal);
$('#delete-submit').on('click', deleteToDo);

function showDeleteModal() {
    var id = $(this).attr('data-id');
    $('#delete-id').val(id);
    $('#delete-modal').modal('show')
}

function deleteToDo() {
    var id = $('#delete-id').val();
    $.ajax({
        url: '/todos/' + id,
        type: 'DELETE',
        success: function () {
            alert('처리되었습니다.');
            $('#delete-modal').modal('hide')
            location.reload();
        },
        error: function () {
            console.log(arguments);
            alert('처리에 실패 했습니다.');
        }
    });
}