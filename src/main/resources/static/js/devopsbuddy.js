// to invoke function only when the document is ready for processing
// this function will wait until the DOM has been fully loaded
$(document).ready(main);

function main() {
    // jquery code
    $('.btn-collapse').click(function (e) {
        e.preventDefault();
        var $this = $(this);
        var $collapse = $this.closest('.collapse-group').find('.collapse');
        $collapse.collapse('toggle');
    });
}